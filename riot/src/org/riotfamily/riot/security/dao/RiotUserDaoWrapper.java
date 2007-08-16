/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.security.dao;

import java.util.Collection;

import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.security.LoginManager;
import org.riotfamily.riot.security.auth.RiotUser;
import org.springframework.dao.DataAccessException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class RiotUserDaoWrapper implements RiotUserDao {

	private RiotUserDao wrappedInstance;
	
	public RiotUserDaoWrapper(RiotUserDao userDao) {
		this.wrappedInstance = userDao;
	}
	
	public RiotUser findUserByCredentials(String username, String password) {
		return wrappedInstance.findUserByCredentials(username, password);
	}

	public Class getEntityClass() {
		return wrappedInstance.getEntityClass();
	}

	public int getListSize(Object parent, ListParams params)
		throws DataAccessException {

		return wrappedInstance.getListSize(parent, params);
	}
	
	public Collection list(Object parent, ListParams params) 
			throws DataAccessException {
		
		return wrappedInstance.list(parent, params);
	}
	
	public String getObjectId(Object entity) {
		return wrappedInstance.getObjectId(entity);
	}

	public Object load(String id) throws DataAccessException {		
		return wrappedInstance.load(id);
	}

	public void save(Object entity, Object parent) throws DataAccessException {
		wrappedInstance.save(entity, parent);		
	}
	
	public void update(Object entity) {		
		wrappedInstance.update(entity);
		RiotUser user = (RiotUser) entity;
		LoginManager.updateUser(user.getUserId(), user);
	}

	public void delete(Object entity, Object parent) {
		wrappedInstance.delete(entity, parent);
		RiotUser user = (RiotUser) entity;
		LoginManager.updateUser(user.getUserId(), null);
	}
	
}