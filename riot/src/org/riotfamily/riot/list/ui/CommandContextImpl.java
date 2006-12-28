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
package org.riotfamily.riot.list.ui;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.command.CommandContext;

class CommandContextImpl implements CommandContext {
	
	private ListSession session;

	private ListItem item;
	
	private Object bean;
	
	private HttpServletRequest request;
	
	public CommandContextImpl(ListSession session, HttpServletRequest request) {
		this.session = session;
		this.request = request;
	}

	public void setItem(ListItem item) {
		this.item = item;
	}

	public Class getBeanClass() {
		return session.getBeanClass();
	}
	
	public RiotDao getDao() {
		return session.getListDefinition().getListConfig().getDao();
	}
	
	public Object getBean() {
		if (bean == null && getObjectId() != null) {
			bean = session.loadBean(getObjectId());
		}
		return bean;
	}
	
	public void setBean(Object bean) {
		this.bean = bean;
	}

	public int getItemsTotal() {
		return 0;
	}

	public ListDefinition getListDefinition() {
		return session.getListDefinition();
	}

	public MessageResolver getMessageResolver() {
		return session.getMessageResolver();
	}

	public String getObjectId() {
		return item.getObjectId();
	}

	public ListParams getParams() {
		return session.getParams();
	}

	public String getParentId() {
		return session.getParentId();
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public int getRowIndex() {
		return item.getRowIndex();
	}

}