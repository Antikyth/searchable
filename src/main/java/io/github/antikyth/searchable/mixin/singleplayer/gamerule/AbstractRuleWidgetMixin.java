/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.singleplayer.gamerule;

import io.github.antikyth.searchable.accessor.singleplayer.gamerule.AbstractRuleWidgetAccessor;
import io.github.antikyth.searchable.config.SearchableConfig;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EditGameRulesScreen.AbstractRuleWidget.class)
public class AbstractRuleWidgetMixin implements AbstractRuleWidgetAccessor {
	@Unique
	protected String query = "";

	@Override
	public void searchable$setQuery(String query) {
		if (enabled() && query != null && !query.equals(this.query)) {
			this.updateHighlight(query);
			this.query = query;
		}
	}

	@Unique
	protected void updateHighlight(String query) {
	}

	@Unique
	@Nullable
	protected String descriptionString;
	@Unique
	protected String technicalName;

	@Unique
	protected final MatchManager descriptionStringMatchManager = new MatchManager();
	@Unique
	protected final MatchManager technicalNameMatchManager = new MatchManager();

	@Unique
	@Override
	public void searchable$setTechnicalName(String technicalName) {
		if (technicalName != null) this.technicalName = technicalName;
	}

	@Override
	public boolean searchable$matches(String query) {
		if (SearchableConfig.INSTANCE.edit_gamerules_screen.match_technical_names.value() && this.technicalName != null) {
			boolean technicalNameMatches = this.technicalNameMatchManager.hasMatches(this.technicalName, query);

			if (technicalNameMatches) return true;
		}

		if (SearchableConfig.INSTANCE.edit_gamerules_screen.match_descriptions.value() && this.descriptionString != null) {
			return this.descriptionStringMatchManager.hasMatches(this.descriptionString, query);
		}

		return false;
	}

	@Unique
	protected boolean enabled() {
		return SearchableConfig.INSTANCE.edit_gamerules_screen.add_search.value();
	}
}
