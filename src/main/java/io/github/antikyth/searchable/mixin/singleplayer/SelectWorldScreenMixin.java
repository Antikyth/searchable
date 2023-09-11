/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.singleplayer;

import io.github.antikyth.searchable.accessor.TextFieldWidgetValidityAccessor;
import io.github.antikyth.searchable.util.Util;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.regex.PatternSyntaxException;

@Mixin(SelectWorldScreen.class)
public class SelectWorldScreenMixin {
	@Unique
	private static final Text SEARCH_BOX_HINT = Util.hint(Text.translatable("selectWorld.search.hint"));

	@Shadow
	protected TextFieldWidget searchBox;

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
	}

	@Inject(method = "method_2744", at = @At("HEAD"))
	private void onSearchBoxChange(String query, CallbackInfo ci) {
		Optional<PatternSyntaxException> valid = MatchManager.matcher().validateQueryError(query);

		((TextFieldWidgetValidityAccessor) this.searchBox).searchable$setValidity(valid);
	}
}
