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
package org.riotfamily.pages.view;

import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.common.web.util.RequestHolder;
import org.riotfamily.common.web.view.freemarker.FacadeTemplateModel;
import org.riotfamily.common.web.view.freemarker.ObjectWrapperPlugin;
import org.riotfamily.common.web.view.freemarker.PluginObjectWrapper;
import org.riotfamily.pages.model.Page;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageObjectWrapperPlugin implements ObjectWrapperPlugin {

	private PathCompleter pathCompleter;

	public void setPathCompleter(PathCompleter pathCompleter) {
		this.pathCompleter = pathCompleter;
	}

	public boolean supports(Object obj) {
		return obj instanceof Page;
	}

	public TemplateModel wrapSupportedObject(Object obj, 
			PluginObjectWrapper wrapper) 
			throws TemplateModelException {
		
		Page page = (Page) obj;
		PageFacade facade = new PageFacade(page, RequestHolder.getRequest(), pathCompleter);
		return new FacadeTemplateModel(facade, page, wrapper);
	}

}
