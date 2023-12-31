/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.gamerule;

import io.github.antikyth.searchable.config.SearchableConfig;
import io.github.antikyth.searchable.util.Util;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EditGameRulesScreen.NamedRuleWidget.class)
public abstract class NamedRuleWidgetMixin extends AbstractRuleWidgetMixin {
	@Unique
	private EditGameRulesScreen instance;

	@Shadow
	public List<OrderedText> name;

	@Unique
	private Text nameText;
	@Unique
	private Text technicalNameText;

	@Unique
	private final MatchManager nameMatchManager = new MatchManager();


	@Override
	public void searchable$setTechnicalName(String technicalName) {
		if (technicalName != null && !technicalName.equals(this.technicalName)) {
			this.technicalName = technicalName;

			this.technicalNameText = Util.technicalName(Text.literal(this.technicalName));
			this.updateHighlight();
		}
	}

	@Inject(method = "<init>", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/font/TextRenderer.wrapLines (Lnet/minecraft/text/StringVisitable;I)Ljava/util/List;",
		ordinal = 0
	))
	public void onConstructor(EditGameRulesScreen instance, @Nullable List<OrderedText> description, Text name, CallbackInfo ci) {
		if (!enabled() || !SearchableConfig.INSTANCE.highlight_matches.value()) return;

		this.instance = instance;

		this.nameText = name;
		this.updateHighlight();
	}

	@Override
	protected boolean updateHighlight() {
		if (!super.updateHighlight()) return false;

		var nameWithHighlight = this.nameMatchManager.getHighlightedText(this.nameText, this.query);
		this.name = this.instance.getTextRenderer().wrapLines(nameWithHighlight, 175);

		return true;
	}

	@ModifyArg(method = "drawName", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;IIIZ)I",
		ordinal = 0
	), index = 3)
	private int adjustNameYCoord(int y) {
		if (enabled() && SearchableConfig.INSTANCE.edit_gamerules_screen.show_technical_names.value() && this.technicalNameText != null) {
			return y - 5;
		} else {
			return y;
		}
	}

	@Inject(method = "drawName", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;IIIZ)I",
		ordinal = 0,
		shift = At.Shift.AFTER
	))
	private void drawTechnicalName(GuiGraphics graphics, int y, int x, CallbackInfo ci) {
		if (!enabled()) return;

		if (SearchableConfig.INSTANCE.edit_gamerules_screen.show_technical_names.value() && this.technicalNameText != null) {
			Integer color = Formatting.WHITE.getColorValue();
			assert color != null;

			graphics.drawText(this.instance.getTextRenderer(), (Text) this.technicalNameMatchManager.getHighlightedText(this.technicalNameText, this.query), x, y + 10, color, false);
		}
	}

	@Unique
	@Override
	public boolean searchable$matches(String query) {
		boolean nameMatches = this.nameMatchManager.hasMatches(this.nameText, query);

		if (nameMatches) {
			return true;
		} else {
			return super.searchable$matches(query);
		}
	}
}
