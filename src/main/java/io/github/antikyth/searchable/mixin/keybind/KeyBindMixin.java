package io.github.antikyth.searchable.mixin.keybind;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.GetMatchManagerAccessor;
import io.github.antikyth.searchable.accessor.MatchesAccessor;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(KeyBind.class)
public abstract class KeyBindMixin implements MatchesAccessor, GetMatchManagerAccessor {
	@Shadow
	public abstract String getTranslationKey();

	@Shadow
	public abstract Text getKeyName();

	@Unique
	private final MatchManager boundKeyMatchManager = new MatchManager();
	@Unique
	private final MatchManager bindNameMatchManager = new MatchManager();

	@Unique
	@Override
	public MatchManager searchable$getMatchManager() {
		return this.bindNameMatchManager;
	}

	@Unique
	@Override
	public boolean searchable$matches(String query) {
		if (matchBoundKey()) {
			Boolean boundKeyMatches = this.boundKeyMatchManager.hasMatches(this.getKeyName(), query);

			if (boundKeyMatches == null || boundKeyMatches) return boundKeyMatches;
		}

		return this.bindNameMatchManager.hasMatches(I18n.translate(this.getTranslationKey()), query);
	}

	@Unique
	private static boolean matchBoundKey() {
		return Searchable.config.keybinds.matchBoundKey;
	}
}
