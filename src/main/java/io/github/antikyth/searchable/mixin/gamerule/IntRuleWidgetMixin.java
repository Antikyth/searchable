/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.gamerule;

import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EditGameRulesScreen.IntRuleWidget.class)
public class IntRuleWidgetMixin extends AbstractRuleWidgetMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConstructor(EditGameRulesScreen instance, Text name, List<OrderedText> tooltip, String description, GameRules.IntRule rule, CallbackInfo ci) {
		this.descriptionString = description;
	}
}
