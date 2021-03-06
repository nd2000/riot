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
package org.riotfamily.riot.list.command.core;

import java.util.Iterator;
import java.util.List;

import org.riotfamily.riot.editor.AbstractObjectEditorDefinition;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.IntermediateDefinition;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.ui.EditorReference;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.GotoUrlResult;
import org.springframework.util.Assert;


/**
 *
 */
public class StepIntoCommand extends AbstractCommand {

	public static final String ACTION_STEP_INTO = "stepInto";

	@Override
	public String getAction() {
		return ACTION_STEP_INTO;
	}

	@Override
	public boolean isEnabled(CommandContext context) {
		return getTargetUrl(context) != null;
	}

	public CommandResult execute(CommandContext context) {
		return new GotoUrlResult(context, getTargetUrl(context));
	}

	private static String getTargetUrl(CommandContext context) {
		EditorDefinition def = context.getListDefinition().getDisplayDefinition();
		Assert.notNull(def, "A DisplayDefinition must be set");

		if (def instanceof IntermediateDefinition) {
			ListDefinition listDef = ((IntermediateDefinition) def).getNestedListDefinition();
			return listDef.getEditorUrl(null, context.getObjectId(), context.getListDefinition().getId());
		}
		else {
			Assert.isInstanceOf(AbstractObjectEditorDefinition.class, def);
			AbstractObjectEditorDefinition displayDef = (AbstractObjectEditorDefinition) def;

			List<EditorReference> childRefs = displayDef.getChildEditorReferences(
					context.getBean(), context.getMessageResolver());

			Iterator<EditorReference> it = childRefs.iterator();
			while (it.hasNext()) {
				EditorReference ref = it.next();
				return ref.getEditorUrl();
			}
			return null;
		}
	}

}
