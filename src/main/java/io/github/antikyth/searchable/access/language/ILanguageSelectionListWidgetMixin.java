/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.access.language;

import net.minecraft.client.gui.screen.option.LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry;
import net.minecraft.client.resource.language.LanguageDefinition;

import java.util.Map;

public interface ILanguageSelectionListWidgetMixin {
	void searchable$filter(String query, Map<String, LanguageDefinition> languages);

	LanguageEntry searchable$getSelectedLanguage();
}
