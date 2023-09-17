/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.select_world;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.TextFieldWidgetValidityAccessor;
import io.github.antikyth.searchable.config.SearchableConfig;
import io.github.antikyth.searchable.gui.widget.SearchableConfigButton;
import io.github.antikyth.searchable.util.Util;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.PatternSyntaxException;

@Mixin(SelectWorldScreen.class)
public abstract class SelectWorldScreenMixin extends Screen {
	@Unique
	private static final Text SEARCH_BOX_HINT = Util.hint(Text.translatable("selectWorld.search.hint"));

	@Shadow
	protected TextFieldWidget searchBox;

	protected SelectWorldScreenMixin(Text title) {
		super(title);
	}

	@ModifyArg(method = "init", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/widget/TextFieldWidget.<init> (Lnet/minecraft/client/font/TextRenderer;IIIILnet/minecraft/client/gui/widget/TextFieldWidget;Lnet/minecraft/text/Text;)V",
		ordinal = 0
	), index = 1)
	private int adjustSearchBoxX(int x) {
		return x + 100 - (Searchable.SEARCH_BOX_WIDTH / 2);
	}

	@ModifyArg(method = "init", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/widget/TextFieldWidget.<init> (Lnet/minecraft/client/font/TextRenderer;IIIILnet/minecraft/client/gui/widget/TextFieldWidget;Lnet/minecraft/text/Text;)V",
		ordinal = 0
	), index = 2)
	private int adjustSearchBoxY(int y) {
		return Searchable.SEARCH_BOX_Y + (y - 22);
	}

	@ModifyArg(method = "init", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/widget/TextFieldWidget.<init> (Lnet/minecraft/client/font/TextRenderer;IIIILnet/minecraft/client/gui/widget/TextFieldWidget;Lnet/minecraft/text/Text;)V",
		ordinal = 0
	), index = 3)
	private int adjustSearchBoxWidth(int width) {
		return Searchable.searchBoxWidth();
	}

	/**
	 * Add a hint to the world selection screen's search box.
	 */
	@Inject(method = "init", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/widget/TextFieldWidget.setChangedListener (Ljava/util/function/Consumer;)V",
		ordinal = 0,
		shift = At.Shift.AFTER
	))
	protected void onInit(CallbackInfo ci) {
		this.searchBox.setHint(SEARCH_BOX_HINT);

		if (SearchableConfig.INSTANCE.show_config_button.value()) {
			this.addDrawableChild(new SearchableConfigButton(
				this.searchBox.getX() + this.searchBox.getWidth() + Searchable.CONFIG_BUTTON_OFFSET,
				this.searchBox.getY() + ((this.searchBox.getHeight() - SearchableConfigButton.CONFIG_BUTTON_SIZE) / 2),
				this
			));
		}
	}

	@Inject(method = "method_2744", at = @At("HEAD"))
	private void onSearchBoxChange(String query, CallbackInfo ci) {
		PatternSyntaxException validityError = MatchManager.matcher().validateQueryError(query);

		((TextFieldWidgetValidityAccessor) this.searchBox).searchable$setValidity(validityError);
	}
}
