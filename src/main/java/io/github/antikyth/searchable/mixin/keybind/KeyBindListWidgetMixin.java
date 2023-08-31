package io.github.antikyth.searchable.mixin.keybind;

import io.github.antikyth.searchable.Searchable;
import net.minecraft.client.gui.widget.option.KeyBindListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(KeyBindListWidget.class)
public class KeyBindListWidgetMixin {
	@ModifyArg(method = "<init>", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/widget/ElementListWidget.<init> (Lnet/minecraft/client/MinecraftClient;IIIII)V"
	), index = 3)
	private static int adjustTopCoord(int top) {
		Searchable.LOGGER.debug("moving keybinds list down by 28px...");

		// 12 pixels lower to match the normal header height (32px), then 16 lower to make space for the search box.
		return top + 12 + 16;
	}
}
