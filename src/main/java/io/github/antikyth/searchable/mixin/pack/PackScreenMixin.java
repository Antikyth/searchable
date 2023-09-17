/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.pack;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.TextFieldWidgetValidityAccessor;
import io.github.antikyth.searchable.config.SearchableConfig;
import io.github.antikyth.searchable.gui.widget.SearchableConfigButton;
import io.github.antikyth.searchable.util.Util;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
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

@Mixin(PackScreen.class)
public abstract class PackScreenMixin extends Screen {
	@Unique
	private static final Text SEARCH_BOX_NARRATION_MESSAGE = Text.translatable("pack.search");
	@Unique
	private static final Text SEARCH_BOX_HINT = Util.hint(Text.translatable("pack.search.hint"));

	@Unique
	private TextFieldWidget searchBox;
	@Unique
	private String query = "";

	@Unique
	private final MatchManager matchManager = new MatchManager();

	@Unique
	private int titleY = 8;

	protected PackScreenMixin(Text title) {
		super(title);
	}

	@Shadow
	protected abstract void updatePackLists();

	@Inject(method = "init", at = @At("HEAD"))
	protected void onInit(CallbackInfo ci) {
		if (disabled()) return;

		Searchable.LOGGER.debug("adding search box to packs screen...");

		// Search box {{{
		this.searchBox = new TextFieldWidget(this.textRenderer, (this.width - Searchable.SEARCH_BOX_WIDTH) / 2, Searchable.SEARCH_BOX_Y, Searchable.searchBoxWidth(), 20, this.searchBox, SEARCH_BOX_NARRATION_MESSAGE);
		this.searchBox.setHint(SEARCH_BOX_HINT);
		this.searchBox.setChangedListener(query -> {
			if (query != null) {

				PatternSyntaxException validityError = MatchManager.matcher().validateQueryError(query);
				((TextFieldWidgetValidityAccessor) this.searchBox).searchable$setValidity(validityError);

				if (!query.equals(this.query)) {
					this.query = query;

					this.updatePackLists();
				}
			}
		});

		this.addSelectableChild(this.searchBox);
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

	@ModifyArg(method = "render", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredShadowedText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V",
		ordinal = 0
	), index = 3)
	public int trackTitleYCoord(int y) {
		if (this.titleY != y) this.titleY = y;

		return y;
	}

	@ModifyArg(method = "render", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredShadowedText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V",
		ordinal = 1
	), index = 3)
	public int adjustDropInfoTextYCoord(int y) {
		if (disabled()) return y;

		int searchBoxPadding = Searchable.SEARCH_BOX_Y - (this.titleY + 8);
		int shift = searchBoxPadding + 20;

		Searchable.LOGGER.debug("moving select packs screen drop info text down by " + shift + "px...");

		return y + shift;
	}

	@Inject(method = "render", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/screen/Screen.render (Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
		ordinal = 0
	))
	public void onRender(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (disabled()) return;

		this.searchBox.render(graphics, mouseX, mouseY, delta);
	}

	@Inject(method = "method_29672", at = @At("HEAD"), cancellable = true)
	private void filterPack(PackListWidget packListWidget, String selectedPack, ResourcePackOrganizer.Pack pack, CallbackInfo ci) {
		if (!disabled() && !this.packMatches(pack)) ci.cancel();
	}

	@Unique
	private boolean packMatches(ResourcePackOrganizer.Pack pack) {
		return this.matchManager.hasMatches(pack.getDisplayName(), this.searchBox.getText())
			|| (matchDescriptions() && this.matchManager.hasMatches(pack.getDescription(), this.searchBox.getText()));
	}

	@Unique
	private static boolean matchDescriptions() {
		return SearchableConfig.INSTANCE.select_packs_screen.match_descriptions.value();
	}

	@Unique
	private static boolean disabled() {
		return !SearchableConfig.INSTANCE.select_packs_screen.add_search.value();
	}
}
