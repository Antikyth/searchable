package io.github.antikyth.searchable.mixin;

import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@ClientOnly
@Mixin(targets = "net.minecraft.client.gui.screen.option.LanguageOptionsScreen$LanguageSelectionListWidget")
public class LanguageSelectionListMixin {
	// Moves the top of the language selection list down by 16 pixels to make space for the search box.
	@ModifyArg(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/gui/widget/AlwaysSelectedEntryListWidget;<init>(Lnet/minecraft/client/MinecraftClient;IIIII)V"
	), index = 3)
	private static int adjustTopCoord(int top) {
		// The world selection screen has the top of its selection list 16 pixels lower to make space for its search
		// box.
		return top + 16;
	}
}
