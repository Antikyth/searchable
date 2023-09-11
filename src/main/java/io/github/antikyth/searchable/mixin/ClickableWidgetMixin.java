/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Makes no changes to {@link ClickableWidget} but enables {@link TextFieldWidgetMixin} to override its handlers.
 */
@Mixin(ClickableWidget.class)
public abstract class ClickableWidgetMixin {
	public ClickableWidgetMixin(int x, int y, int width, int height, Text message) {
	}

	@ModifyExpressionValue(method = "updateTooltip", at = @At(
		value = "FIELD",
		opcode = Opcodes.GETFIELD,
		target = "net/minecraft/client/gui/widget/ClickableWidget.tooltip : Lnet/minecraft/client/gui/tooltip/Tooltip;",
		ordinal = 0
	))
	@Nullable
	protected Tooltip switchTooltipInUpdateTooltipCondition(@Nullable Tooltip tooltip) {
		return tooltip;
	}

	@ModifyArg(method = "updateTooltip", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/screen/Screen.setDeferredTooltip (Lnet/minecraft/client/gui/tooltip/Tooltip;Lnet/minecraft/client/gui/tooltip/TooltipPositioner;Z)V",
		ordinal = 0
	), index = 0)
	protected Tooltip switchTooltipInSetDeferredTooltipCall(@NotNull Tooltip tooltip) {
		return tooltip;
	}

	@ModifyExpressionValue(method = "appendNarrations", at = @At(
		value = "FIELD",
		opcode = Opcodes.GETFIELD,
		target = "net/minecraft/client/gui/widget/ClickableWidget.tooltip : Lnet/minecraft/client/gui/tooltip/Tooltip;",
		ordinal = 0
	))
	@Nullable
	protected Tooltip switchTooltipInAppendNarrationsCondition(@Nullable Tooltip tooltip) {
		return tooltip;
	}

	@ModifyReceiver(method = "appendNarrations", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/tooltip/Tooltip.appendNarrations (Lnet/minecraft/client/gui/screen/narration/NarrationMessageBuilder;)V",
		ordinal = 0
	))
	protected Tooltip switchTooltipInAppendNarrationsCall(Tooltip tooltip, NarrationMessageBuilder builder) {
		return tooltip;
	}
}
