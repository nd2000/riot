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
 *   alf
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.support;

import java.util.List;

import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.Order;

/**
 * @author alf
 * @since 6.5
 */
public class EmptyListParams implements ListParams {

	public Object getFilter() {
		return null;
	}

	public String[] getFilteredProperties() {
		return null;
	}

	public int getOffset() {
		return 0;
	}

	public List<Order> getOrder() {
		return null;
	}

	public int getPageSize() {
		return 0;
	}
	public String getSearch() {
		return null;
	}

	public String[] getSearchProperties() {
		return null;
	}

	public boolean hasOrder() {
		return false;
	}
}
