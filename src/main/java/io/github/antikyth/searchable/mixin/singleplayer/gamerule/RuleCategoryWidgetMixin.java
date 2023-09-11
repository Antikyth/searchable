/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.singleplayer.gamerule;

import io.github.antikyth.searchable.config.SearchableConfig;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EditGameRulesScreen.RuleCategoryWidget.class)
public class RuleCategoryWidgetMixin extends AbstractRuleWidgetMixin {
	@Unique
	private final MatchManager matchManager = new MatchManager();

	@ModifyArg(method = "render", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/GuiGraphics.drawCenteredShadowedText (Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V",
		ordinal = 0
	), index = 1)
	private Text drawNameWithHighlight(Text name) {
		if (enabled()) {
			return (Text) this.matchManager.getHighlightedText(name, this.query);
		}

		return name;
	}

	@Override
	protected boolean enabled() {
		return super.enabled() && SearchableConfig.INSTANCE.highlight_matches.value() && SearchableConfig.INSTANCE.edit_gamerules_screen.match_categories.value();
	}
}
