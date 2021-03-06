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
package org.riotfamily.riot.list.command.export;

import java.util.List;
import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.GotoUrlResult;

public class ExportCommand extends AbstractCommand {

	public static final String ACTION_EXPORT = "export";

	private Exporter exporter;
	
	private List<String> properties;

	@Override
	public String getAction() {
		return ACTION_EXPORT;
	}
	
	public Exporter getExporter() {
		return exporter;
	}

	public void setExporter(Exporter exporter) {
		this.exporter = exporter;
	}

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	public List<String> getProperties() {
		return this.properties;
	}
	
	public CommandResult execute(CommandContext context) {
		Map<String, String> attributes = Generics.newHashMap();
		attributes.put("commandId", getId());
		attributes.put("listSessionKey", context.getListSessionKey());
		attributes.put("objectId", context.getObjectId());
		String url = getRuntime().getUrlForHandler("exportController", attributes);
		return new GotoUrlResult(context, url);
	}

}
