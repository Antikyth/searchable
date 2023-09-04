/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.keybind;

import io.github.antikyth.searchable.access.ISetQuery;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.KeyBindsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.option.KeyBindListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindsScreen.class)
public class KeyBindScreenMixin extends GameOptionsScreen {
	@Shadow
	private KeyBindListWidget keyBindList;

	@Unique
	public TextFieldWidget searchBox;

	// Mixin will ignore this - required because of extending `GameOptionsScreen`
	public KeyBindScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
		super(parent, gameOptions, title);
	}

	@Inject(method = "init", at = @At("HEAD"))
	public void onInit(CallbackInfo ci) {
		this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox, Text.translatable("controls.keybinds.search"));
		this.searchBox.setChangedListener(query -> ((ISetQuery) this.keyBindList).searchable$setQuery(query));

		this.addSelectableChild(this.searchBox);
		this.setInitialFocus(this.searchBox);
	}

	@Inject(method = "render", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/widget/option/KeyBindListWidget;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
			shift = At.Shift.AFTER
	))
	public void onRender(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		this.searchBox.drawWidget(graphics, mouseX, mouseY, delta);
	}
}
