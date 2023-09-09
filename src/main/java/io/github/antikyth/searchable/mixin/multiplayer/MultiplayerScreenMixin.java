/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.multiplayer;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.accessor.TextFieldWidgetValidityAccessor;
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

	@ModifyArg(method = "<init>", at = @At(
		value = "INVOKE",
		target = "net/minecraft/text/Text.translatable (Ljava/lang/String;)Lnet/minecraft/text/MutableText;",
		ordinal = 0
	), index = 0)
	private static String changeSelectServerTitle(String title) {
		if (Searchable.config.selectServer.changeSelectServerTitle) {
			Searchable.LOGGER.debug("changing server selection screen title translation key to {}", ALT_TITLE_TRANSLATION_KEY);

			return ALT_TITLE_TRANSLATION_KEY;
		} else {
			return title;
		}
	}

	@Inject(method = "init", at = @At("HEAD"))
	private void onInit(CallbackInfo ci) {
		if (!enabled()) return;

		Searchable.LOGGER.debug("adding search box to multiplayer servers screen...");

		this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox, SEARCH_BOX_NARRATION_MESSAGE);
		this.searchBox.setHint(SEARCH_BOX_HINT);
		this.searchBox.setChangedListener(query -> {
			boolean valid = MatchManager.matcher().validateQuery(query);

			((TextFieldWidgetValidityAccessor) this.searchBox).searchable$setValidity(valid);

			if (!valid) query = "";
			((SetQueryAccessor) this.serverListWidget).searchable$setQuery(query);
		});

		this.addSelectableChild(this.searchBox);
		// Set the search box to be the initial focus.  This is to be consistent with the behavior of the world select
		// screen's search box.
		this.setInitialFocus(this.searchBox);
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
		if (!enabled()) return top;

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
		if (!enabled()) return y;

		Searchable.LOGGER.debug("moving multiplayer servers screen title up by 12px...");

		return y - 12;
	}

	@Inject(method = "render", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget.render (Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
		shift = At.Shift.AFTER
	))
	public void onRender(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (!enabled()) return;

		this.searchBox.drawWidget(graphics, mouseX, mouseY, delta);
	}

	@Unique
	private static boolean enabled() {
		return Searchable.config.selectServer.enable;
	}
}
