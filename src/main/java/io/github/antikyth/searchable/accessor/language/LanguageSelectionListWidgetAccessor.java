/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.accessor.language;

import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry;

public interface LanguageSelectionListWidgetAccessor extends SetQueryAccessor {
	LanguageEntry searchable$getSelectedLanguage();
}
