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
package org.riotfamily.website.generic.model;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Abstract base class for ModelBuilders that expose only a single object. 
 * The built model will be a {@link org.riotfamily.common.collection.FlatMap}.
 */
public abstract class BaseModelBuilder implements ModelBuilder {

	private String modelKey;
	
	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}

	public final Map<String, Object> buildModel(HttpServletRequest request) throws Exception {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put(modelKey, buildModelObject(request));
		return model;
	}
	
	public abstract Object buildModelObject(HttpServletRequest request) 
			throws Exception; 

}
