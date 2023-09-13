/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.language;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.GetSearchBoxAccessor;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.accessor.TextFieldWidgetValidityAccessor;
import io.github.antikyth.searchable.accessor.language.LanguageSelectionListWidgetAccessor;
import io.github.antikyth.searchable.config.SearchableConfig;
import io.github.antikyth.searchable.gui.widget.SearchableConfigButton;
import io.github.antikyth.searchable.util.Util;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen.LanguageSelectionListWidget;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.PatternSyntaxException;

@Mixin(LanguageOptionsScreen.class)
public abstract class LanguageOptionsScreenMixin extends GameOptionsScreen implements GetSearchBoxAccessor {
	@Unique
	private static final Text SEARCH_BOX_NARRATION_MESSAGE = Text.translatable("option.language.search");
	@Unique
	private static final Text SEARCH_BOX_HINT = Util.hint(Text.translatable("option.language.search.hint"));

	@Unique
	public TextFieldWidget searchBox;

	@Unique
	@Override
	public TextFieldWidget searchable$getSearchBox() {
		return searchBox;
	}

	@Shadow
	private LanguageSelectionListWidget languageSelectionList;

	// Mixin will ignore this - required because of extending `GameOptionsScreen`
	public LanguageOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
		super(parent, gameOptions, title);
	}

	// Add the search box to the UI
	@Inject(method = "init", at = @At("HEAD"))
	protected void onInitHead(CallbackInfo ci) {
		if (disabled()) return;

		Searchable.LOGGER.debug("adding search box to language options screen...");

		// Search box {{{
		this.searchBox = new TextFieldWidget(this.textRenderer, (this.width - Searchable.SEARCH_BOX_WIDTH) / 2, 22, Searchable.searchBoxWidth(), 20, this.searchBox, SEARCH_BOX_NARRATION_MESSAGE);
		this.searchBox.setHint(SEARCH_BOX_HINT);
		// Filter the language selection list when the query is changed.
		this.searchBox.setChangedListener(query -> {
			PatternSyntaxException validityError = MatchManager.matcher().validateQueryError(query);

			((TextFieldWidgetValidityAccessor) this.searchBox).searchable$setValidity(validityError);

			((SetQueryAccessor) this.languageSelectionList).searchable$setQuery(query);
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

	@Inject(method = "init", at = @At("TAIL"))
	protected void onInitTail(CallbackInfo ci) {
		((SetQueryAccessor) this.languageSelectionList).searchable$setQuery(this.searchBox.getText());
	}

	/* ************************************************************************************************************** *\
	|* The language selection list is moved down in the `LanguageSelectionListMixin`, as its coords are hardcoded in  *|
	|* its constructor.                                                                                               *|
	\* ************************************************************************************************************** */

	// Move the title text up 8 pixels to make room for the search box.
	@ModifyArg(method = "render", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredShadowedText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V",
		ordinal = 0
	), index = 3)
	public int adjustTitleTextYCoord(int y) {
		if (disabled()) return y;

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
		if (disabled()) return;

		this.searchBox.render(graphics, mouseX, mouseY, delta);
	}

	// Only select a language when a toggle key is pressed if the language selection list is focused.
	@ModifyExpressionValue(method = "keyPressed", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/gui/CommonInputs;isToggle(I)Z"
	))
	private boolean onlySelectLanguageIfFocused(boolean original) {
		if (disabled()) return original;

		// Only if the language selection list is focused...
		return original && this.languageSelectionList.equals(this.getFocused());
	}

	// Set the language to the last selected language entry, even if it is hidden by the current query.
	//
	// NOTE: The Minecraft Development plugin has false positive errors here. This is how it is meant to be written.
	@ModifyVariable(method = "onDone", at = @At(
		value = "STORE",
		ordinal = 0
	))
	private LanguageEntry setSelectedLanguageEvenIfHidden(LanguageEntry original) {
		if (disabled()) return original;

		return ((LanguageSelectionListWidgetAccessor) this.languageSelectionList).searchable$getSelectedLanguage();
	}

	@Unique
	private static boolean disabled() {
		return !SearchableConfig.INSTANCE.language_screen.add_search.value();
	}
}
