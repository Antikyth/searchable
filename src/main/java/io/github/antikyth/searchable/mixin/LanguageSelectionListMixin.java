package io.github.antikyth.searchable.mixin;

import io.github.antikyth.searchable.Searchable;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@ClientOnly
@Mixin(LanguageOptionsScreen.LanguageSelectionListWidget.class)
public class LanguageSelectionListMixin {
	// Move the top of the language selection list down by 16 pixels to make space for the search box.
	@ModifyArg(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/gui/widget/AlwaysSelectedEntryListWidget;<init>(Lnet/minecraft/client/MinecraftClient;IIIII)V"
	), index = 3)
	private static int adjustTopCoord(int top) {
		Searchable.LOGGER.debug("moving language selection list down by 16px...");

		// The world selection screen has the top of its selection list 16 pixels lower to make space for its search
		// box.
		return top + 16;
	}
}
