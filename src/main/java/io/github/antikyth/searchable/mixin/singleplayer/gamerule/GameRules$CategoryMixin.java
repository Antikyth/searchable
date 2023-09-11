/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.singleplayer.gamerule;

import io.github.antikyth.searchable.accessor.MatchesAccessor;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRules.Category.class)
public class GameRules$CategoryMixin implements MatchesAccessor {
	@Final
	@Shadow
	private String category;

	/**
	 * For checking if any mixins have changed the category's name.
	 */
	@Unique
	private String name;
	@Unique
	private String translatedName;

	@Unique
	private final MatchManager matchManager = new MatchManager();

	@Override
	public boolean searchable$matches(String query) {
		if (this.category != null && !this.category.equals(this.name)) {
			this.name = this.category;
			this.translatedName = I18n.translate(this.name);
		}

		return this.matchManager.hasMatches(this.translatedName, query);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConstructor(String string, int i, String category, CallbackInfo ci) {
		this.name = this.category;
		this.translatedName = I18n.translate(this.name);
	}
}
