package io.github.antikyth.searchable.mixin.singleplayer.gamerule;

import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EditGameRulesScreen.BooleanRuleWidget.class)
public class BooleanRuleWidgetMixin extends AbstractRuleWidgetMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConstructor(EditGameRulesScreen instance, Text name, List<OrderedText> tooltip, String description, GameRules.BooleanRule rule, CallbackInfo ci) {
		this.descriptionString = description;
	}
}
