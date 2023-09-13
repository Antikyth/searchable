/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.multiplayer;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.accessor.TextFieldWidgetValidityAccessor;
import io.github.antikyth.searchable.config.SearchableConfig;
import io.github.antikyth.searchable.gui.widget.SearchableConfigButton;
import io.github.antikyth.searchable.util.Util;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
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

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
	@Unique
	private static final Text SEARCH_BOX_NARRATION_MESSAGE = Text.translatable("selectServer.search");
	@Unique
	private static final Text SEARCH_BOX_HINT = Util.hint(Text.translatable("selectServer.search.hint"));

	@Unique
	private static final String ALT_TITLE_TRANSLATION_KEY = "selectServer.title";

	@Unique
	public TextFieldWidget searchBox;

	@Shadow
	protected MultiplayerServerListWidget serverListWidget;

	// Mixin will ignore this - required because of extending `Screen`
	protected MultiplayerScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("HEAD"))
	private void onInit(CallbackInfo ci) {
		if (disabled()) return;

		Searchable.LOGGER.debug("adding search box to multiplayer servers screen...");

		// Search box {{{
		this.searchBox = new TextFieldWidget(this.textRenderer, (this.width - Searchable.SEARCH_BOX_WIDTH) / 2, 22, Searchable.searchBoxWidth(), 20, this.searchBox, SEARCH_BOX_NARRATION_MESSAGE);
		this.searchBox.setHint(SEARCH_BOX_HINT);
		this.searchBox.setChangedListener(query -> {
			PatternSyntaxException validityError = MatchManager.matcher().validateQueryError(query);

			((TextFieldWidgetValidityAccessor) this.searchBox).searchable$setValidity(validityError);

			((SetQueryAccessor) this.serverListWidget).searchable$setQuery(query);
		});

		this.addSelectableChild(this.searchBox);
		// Set the search box to be the initial focus.  This is to be consistent with the behavior of the world select
		// screen's search box.
		this.setInitialFocus(this.searchBox);
		// }}}

		// Config button {{{
		if (SearchableConfig.INSTANCE.show_config_button.value()) {
			this.addDrawableChild(new SearchableConfigButton(
				this.searchBox.getX() + this.searchBox.getWidth() + Searchable.CONFIG_BUTTON_OFFSET,
				this.searchBox.getY() + ((this.searchBox.getHeight() - SearchableConfigButton.CONFIG_BUTTON_SIZE) / 2),
				this
			));
		}
		// }}}
	}

	// Move the server list down by 16px to make room for the search box.
	@ModifyArg(method = "init", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget.<init> (Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;Lnet/minecraft/client/MinecraftClient;IIIII)V"
	), index = 4)
	private int adjustServerListTopCoordConstructor(int top) {
		return adjustServerListTopCoord(top);
	}

	@ModifyArg(method = "init", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget.updateSize (IIII)V"
	), index = 2)
	private int adjustServerListTopCoordUpdateSize(int top) {
		return adjustServerListTopCoord(top);
	}

	@Unique
	private int adjustServerListTopCoord(int top) {
		if (disabled()) return top;

		Searchable.LOGGER.debug("moving multiplayer servers screen server list down by 16px...");

		return top + 16;
	}

	// Move the title text up 12 pixels to make room for the search box.
	@ModifyArg(method = "render", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredShadowedText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V",
		ordinal = 0
	), index = 3)
	public int adjustTitleTextYCoord(int y) {
		if (disabled()) return y;

		Searchable.LOGGER.debug("moving multiplayer servers screen title up by 12px...");

		return y - 12;
	}

	@Inject(method = "render", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget.render (Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
		shift = At.Shift.AFTER
	))
	public void onRender(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (disabled()) return;

		this.searchBox.render(graphics, mouseX, mouseY, delta);
	}

	@Unique
	private static boolean disabled() {
		return !SearchableConfig.INSTANCE.select_server_screen.add_search.value();
	}
}
