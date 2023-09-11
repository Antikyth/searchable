package io.github.antikyth.searchable.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.antikyth.searchable.accessor.TextFieldWidgetValidityAccessor;
import io.github.antikyth.searchable.util.Colors;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.regex.PatternSyntaxException;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin extends ClickableWidgetMixin implements TextFieldWidgetValidityAccessor {
	@Unique
	private static final int INVALID_COLOR = Colors.RED;

	@Unique
	private Optional<PatternSyntaxException> validityError = Optional.empty();

	@Unique
	private Tooltip validityErrorTooltip;

	public TextFieldWidgetMixin(int x, int y, int width, int height, Text message) {
		super(x, y, width, height, message);
	}

	@Override
	public void searchable$setValidity(Optional<PatternSyntaxException> validityError) {
		if (validityError.isPresent()) {
			var error = validityError.get();

			this.validityErrorTooltip = Tooltip.create(Text.literal(error.getMessage()).formatted(Formatting.RED), Text.literal(error.getDescription()));
		} else {
			this.validityErrorTooltip = null;
		}

		this.validityError = validityError;
	}

	@ModifyExpressionValue(method = "drawWidget", at = @At(
		value = "FIELD",
		target = "net/minecraft/client/gui/widget/TextFieldWidget.editableColor : I",
		opcode = Opcodes.GETFIELD,
		ordinal = 0
	))
	private int drawTextWithInvalidColor(int color) {
		return this.validityError.isEmpty() ? color : INVALID_COLOR;
	}

	@Override
	protected @Nullable Tooltip switchTooltipInUpdateTooltipCondition(@Nullable Tooltip tooltip) {
		return this.validityError.isPresent() ? this.validityErrorTooltip : tooltip;
	}

	@Override
	protected Tooltip switchTooltipInSetDeferredTooltipCall(@NotNull Tooltip tooltip) {
		return this.validityError.isPresent() ? this.validityErrorTooltip : tooltip;
	}

	@Override
	protected @Nullable Tooltip switchTooltipInAppendNarrationsCondition(@Nullable Tooltip tooltip) {
		return this.validityError.isPresent() ? this.validityErrorTooltip : tooltip;
	}

	@Override
	protected Tooltip switchTooltipInAppendNarrationsCall(Tooltip tooltip, NarrationMessageBuilder builder) {
		return this.validityError.isPresent() ? this.validityErrorTooltip : tooltip;
	}
}
