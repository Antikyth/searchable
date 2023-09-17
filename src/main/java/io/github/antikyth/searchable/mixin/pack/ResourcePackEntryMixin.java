/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.pack;

import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.config.SearchableConfig;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PackListWidget.ResourcePackEntry.class)
public abstract class ResourcePackEntryMixin extends AlwaysSelectedEntryListWidget.Entry<PackListWidget.ResourcePackEntry> implements SetQueryAccessor {
	@Unique
	private String query = "";

	@Override
	public void searchable$setQuery(String query) {
		if (enabled() && query != null && !query.equals(this.query)) {
			this.query = query;
		}
	}

	// TODO: render highlights, as it requires highlighting `OrderedText`s

	@Unique
	private static boolean enabled() {
		return SearchableConfig.INSTANCE.select_packs_screen.add_search.value()
			&& SearchableConfig.INSTANCE.highlight_matches.value();
	}
}
