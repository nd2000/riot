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
package org.riotfamily.statistics.dao;

import org.hibernate.SessionFactory;
import org.riotfamily.statistics.domain.Statistics;

public class HibernateCacheStatisticsDao extends AbstractSimpleStatsDao {

	private SessionFactory sessionFactory;
	
	public HibernateCacheStatisticsDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	protected void populateStats(Statistics stats) throws Exception {
		org.hibernate.stat.Statistics hs = sessionFactory.getStatistics();
		
		stats.add("Query cache hit count", hs.getQueryCacheHitCount());
		stats.add("Query cache miss count", hs.getQueryCacheMissCount());
		stats.add("Query cache put count", hs.getQueryCachePutCount());

		stats.add("2nd level cache hit count" , hs.getSecondLevelCacheHitCount());
		stats.add("2nd level cache miss count", hs.getSecondLevelCacheMissCount());
		stats.add("2nd level cache put count", hs.getSecondLevelCachePutCount());
	}
	
}
