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
 *   Jan-Frederic Linde [jfl at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.form.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.options.FormAwareOptionsModel;
import org.riotfamily.riot.security.AccessController;

/**
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class RestrictedOptionsModel implements FormAwareOptionsModel {
	
	private Object model;
	
	public RestrictedOptionsModel(Object model) {
		this.model = model;
	}

	public Collection getOptionsValues(Form form) {
		Collection sourceOptions = form.getOptionValues(model);
		ArrayList result = new ArrayList();
		Iterator it = sourceOptions.iterator();
		while (it.hasNext()) {
			Object option = it.next();
			if (AccessController.isGranted("use-option", option)) {
				result.add(option);
			}
		}
		return result;
	}

	

}
