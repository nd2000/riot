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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.hibernate.dao;

import java.util.Collections;
import java.util.List;

import org.hibernate.SessionFactory;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.SwappableItemDao;
import org.riotfamily.riot.list.support.ListParamsImpl;

public class HqlIndexedListDao extends HqlCollectionDao 
		implements SwappableItemDao {

	public HqlIndexedListDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void swapEntity(Object entity, Object parent, 
    		ListParams params, int swapWith) {
    	
    	List<?> items = listInternal(parent, new ListParamsImpl(params));
    	int i = items.indexOf(entity);
    	Object nextItem = items.get(i + swapWith);
    	
    	List<?> list = (List<?>) getCollection(parent);
    	Collections.swap(list, list.indexOf(entity), list.indexOf(nextItem));
	}
}
