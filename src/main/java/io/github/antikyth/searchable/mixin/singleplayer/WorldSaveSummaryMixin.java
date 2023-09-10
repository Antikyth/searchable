package io.github.antikyth.searchable.mixin.singleplayer;

import io.github.antikyth.searchable.accessor.GetMatchManagerAccessor;
import io.github.antikyth.searchable.accessor.MatchesAccessor;
import io.github.antikyth.searchable.config.SearchableConfig;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.text.Text;
import net.minecraft.world.storage.WorldSaveSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WorldSaveSummary.class)
public abstract class WorldSaveSummaryMixin implements GetMatchManagerAccessor, MatchesAccessor {
	@Shadow
	public abstract String getDisplayName();

	@Shadow
	public abstract String getName();

	@Shadow
	public abstract Text getDetails();

	@Unique
	private final MatchManager worldDisplayNameMatchManager = new MatchManager();
	@Unique
	private final MatchManager worldNameMatchManager = new MatchManager();
	@Unique
	private final MatchManager worldDetailsMatchManager = new MatchManager();

	@Override
	public MatchManager searchable$getMatchManager() {
		return null;
	}

	@Override
	public boolean searchable$matches(String query) {
		return this.worldDisplayNameMatchManager.hasMatches(this.getDisplayName(), query)
		       || this.worldNameMatchManager.hasMatches(this.getName(), query)
		       || (matchDetails() && this.worldDetailsMatchManager.hasMatches(this.getDetails(), query));
	}

	@Unique
	private static boolean matchDetails() {
		return SearchableConfig.INSTANCE.select_world_screen.match_world_details.value();
	}
}
