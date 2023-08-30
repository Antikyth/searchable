package io.github.antikyth.searchable.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.antikyth.searchable.Searchable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageOptionsScreen.class)
public abstract class LanguageOptionsScreenMixin extends GameOptionsScreen {
	@Unique
	private TextFieldWidget searchBox;

	@Shadow
	private LanguageOptionsScreen.LanguageSelectionListWidget languageSelectionList;

	// Mixin will ignore this - required because of extending `GameOptionsScreen`
	public LanguageOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
		super(parent, gameOptions, title);
	}

	// Add the search box to the UI
	@Inject(method = "init", at = @At("HEAD"))
	public void onInit(CallbackInfo ci) {
		Searchable.LOGGER.debug("adding search box to language options screen...");

		// Search box coordinates and size copied from the world selection screen.
		this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 208, 20, this.searchBox, Text.translatable("option.language.search"));
		this.addSelectableChild(this.searchBox);

		// Set the search box to be the initial focus.  This is to be consistent with the behavior of the world select
		// screen's search box.
		this.setInitialFocus(this.searchBox);
	}

	/******************************************************************************************************************\
	 * The language selection list is moved down in the `LanguageSelectionListMixin`, as its coords are hardcoded in    *
	 * its constructor.                                                                                                 *
	 \******************************************************************************************************************/

	// Move the title text up 8 pixels to make room for the search box.
	@ModifyArg(method = "render", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredShadowedText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V",
		ordinal = 0
	), index = 3)
	public int adjustTitleTextYCoord(int y) {
		Searchable.LOGGER.debug("moving language selection screen title up by 8px...");

		// The world selection screen has its title 8 pixels higher to make room for its search box.
		return y - 8;
	}

	// Render the search box (after the language selection list has been rendered, so the search box isn't hidden by the
	// background)
	@Inject(method = "render", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/gui/screen/option/LanguageOptionsScreen$LanguageSelectionListWidget;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
		shift = At.Shift.AFTER
	))
	public void onRender(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		this.searchBox.drawWidget(graphics, mouseX, mouseY, delta);
	}

	// Only select a language when a toggle key is pressed if the language selection list is focused.
	@ModifyExpressionValue(method = "keyPressed", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/gui/CommonInputs;isToggle(I)Z"
	))
	private boolean onlySelectLanguageIfFocused(boolean original) {
		return original && this.languageSelectionList.equals(this.getFocused());
	}
}
