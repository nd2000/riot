/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.riot.job.support;

import java.util.HashSet;

import org.riotfamily.common.util.Generics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.riotfamily.riot.job.model.JobDetail;

public class TaskList {

	private Logger log = LoggerFactory.getLogger(TaskList.class);
	
	private HashSet<JobTask> activeTasks = Generics.newHashSet();
	
	/**
	 * Returns the JobTask for the given JobDetail or <code>null</code> if
	 * no such task exists.
	 */
	public synchronized JobTask getJobTask(JobDetail detail) {
		for (JobTask task : activeTasks) {
			if (task.getDetail().getId().equals(detail.getId())) {
				return task;
			}
		}
		return null;
	}
	
	/**
	 * Adds the given task to the list of active tasks and notifies waiting
	 * threads.
	 */
	public synchronized void addTask(JobTask task) {
		activeTasks.add(task);
		notifyAll();
	}
	
	/**
	 * Removes the given task from the list of active tasks.
	 */
	public synchronized void removeTask(JobTask task) {
		activeTasks.remove(task);
	}
		
	public void interrupt(JobDetail detail) {
		JobTask task = getJobTask(detail);
		if (task != null) {
			task.interrupt();
			removeTask(task);
		}
	}
	
	public void interruptAll() {
		//REVISIT Synchronization ...
		for (JobTask task : activeTasks) {
			log.info("Interrupting task " + task.getDetail().getId());
			task.interrupt();
		}
		activeTasks.clear();
	}
	
	/**
	 * Iterates over the active tasks and invokes 
	 * {@link JobTask#updateExecutionTime() task.updateExecutionTime()}.
	 */
	public synchronized void updateExecutionTimes() {
		for (JobTask task : activeTasks) {
			task.updateExecutionTime();
		}
	}
	
	/**
	 * Waits until at least one active task is present.
	 */
	public synchronized void waitForTasks() {
		if (activeTasks.isEmpty()) {
			try {
				wait();
			}
			catch (InterruptedException e) {
			}
		}
	}
	
}
