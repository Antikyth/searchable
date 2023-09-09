package io.github.antikyth.searchable.mixin.singleplayer.gamerule;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.singleplayer.gamerule.AbstractRuleWidgetAccessor;
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
		if (Searchable.config.editGamerule.matchTechnicalName && this.technicalName != null) {
			boolean technicalNameMatches = this.technicalNameMatchManager.hasMatches(this.technicalName, query);

			if (technicalNameMatches) return true;
		}

		if (Searchable.config.editGamerule.matchDescription && this.descriptionString != null) {
			return this.descriptionStringMatchManager.hasMatches(this.descriptionString, query);
		}

		return false;
	}

	@Unique
	protected boolean enabled() {
		return Searchable.config.editGamerule.enable;
	}
}
