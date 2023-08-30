package io.github.antikyth.searchable.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageOptionsScreen.class)
public abstract class LanguageOptionsScreenMixin extends GameOptionsScreen {
	@Unique
	private TextFieldWidget searchBox;

	// Mixin will ignore this - required because of extending `GameOptionsScreen`
	public LanguageOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
		super(parent, gameOptions, title);
	}

	// Add the search box to the UI
	@Inject(method = "init", at = @At("HEAD"))
	public void onInit(CallbackInfo ci) {
		// Search box coordinates and size copied from the world selection screen.
		this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 208, 20, Text.translatable("option.language.search"));
		this.addSelectableChild(this.searchBox);
	}

	/******************************************************************************************************************\
	 * The language selection list is moved down in the `LanguageSelectionListMixin`, as its coords are hardcoded in    *
	 * its constructor.                                                                                                 *
	 \******************************************************************************************************************/

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

	// TODO: modify keyPressed

}
