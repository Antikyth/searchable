/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.gamerule;

import io.github.antikyth.searchable.accessor.singleplayer.gamerule.AbstractRuleWidgetAccessor;
import io.github.antikyth.searchable.config.SearchableConfig;
import io.github.antikyth.searchable.util.Util;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.text.OrderedText;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(EditGameRulesScreen.AbstractRuleWidget.class)
public class AbstractRuleWidgetMixin implements AbstractRuleWidgetAccessor {
	/**
	 * This is the tooltip, not the description. It is named incorrectly. 1.20.2 mappings will have this fixed (by
	 * Antikyth, writer of this javadoc).
	 */
	@Shadow
	@Final
	List<OrderedText> description;

	@Unique
	protected List<OrderedText> highlightedTooltip = this.description;

	@Unique
	protected String query = "";

	@Override
	public void searchable$setQuery(String query) {
		if (enabled() && query != null && !query.equals(this.query)) {
			this.updateHighlight(query);
			this.query = query;
		}
	}

	/**
	 * Updates the highlighting for the rule widget.
	 *
	 * @return Whether highlighting was able to be applied.
	 */
	@Unique
	protected boolean updateHighlight(String query) {
		if (!enabled() || !SearchableConfig.INSTANCE.highlight_matches.value()) return false;

		// TODO: What if you only want to highlight part of the tooltip (e.g. just the name)?
		if (this.description != null) {
			this.highlightedTooltip = this.description.stream().map(orderedText -> {
				String string = Util.orderedTextToString(orderedText);

				return this.tooltipMatchManager.getHighlightedText(orderedText, string, query);
			}).collect(Collectors.toList());
		}

		return true;
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
	protected final MatchManager tooltipMatchManager = new MatchManager();

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
