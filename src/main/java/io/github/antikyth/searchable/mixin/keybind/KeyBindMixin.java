package io.github.antikyth.searchable.mixin.keybind;

import io.github.antikyth.searchable.accessor.GetMatchManagerAccessor;
import io.github.antikyth.searchable.accessor.MatchesAccessor;
import io.github.antikyth.searchable.config.SearchableConfig;
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
			if (this.boundKeyMatchManager.hasMatches(this.getKeyName(), query)) return true;
		}

		return this.bindNameMatchManager.hasMatches(I18n.translate(this.getTranslationKey()), query);
	}

	@Unique
	private static boolean matchBoundKey() {
		return SearchableConfig.INSTANCE.keybinds_screen.match_bound_keys.value();
	}
}
