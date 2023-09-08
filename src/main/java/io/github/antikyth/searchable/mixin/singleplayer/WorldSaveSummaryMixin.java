package io.github.antikyth.searchable.mixin.singleplayer;

import io.github.antikyth.searchable.accessor.GetMatchManagerAccessor;
import io.github.antikyth.searchable.accessor.MatchesAccessor;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.text.Text;
import net.minecraft.world.storage.WorldSaveSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WorldSaveSummary.class)
public abstract class WorldSaveSummaryMixin implements GetMatchManagerAccessor, MatchesAccessor {
	@Shadow
	public abstract Text getDetails();

	@Unique
	private final MatchManager worldDetailsMatchManager = new MatchManager();

	@Override
	public MatchManager searchable$getMatchManager() {
		return null;
	}

	/**
	 * Whether the summary's {@linkplain WorldSaveSummary#getDetails() details} match the given {@code query}.
	 */
	@Override
	public boolean searchable$matches(String query) {
		return this.worldDetailsMatchManager.hasMatches(this.getDetails(), query);
	}
}
