package io.github.antikyth.searchable.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.antikyth.searchable.accessor.TextFieldWidgetValidityAccessor;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.regex.PatternSyntaxException;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin extends ClickableWidget implements TextFieldWidgetValidityAccessor {
	@Unique
	private static final int INVALID_COLOR;

	static {
		Integer invalidColor = Formatting.RED.getColorValue();

		INVALID_COLOR = invalidColor == null ? TextFieldWidget.DEFAULT_EDITABLE_COLOR : invalidColor;
	}

	@Unique
	private Optional<PatternSyntaxException> validityError = Optional.empty();
//	@Unique
//	private Tooltip currentTooltip;
//	@Unique
//	private Tooltip validityErrorTooltip;

	public TextFieldWidgetMixin(int x, int y, int width, int height, Text message) {
		super(x, y, width, height, message);
	}

	@Override
	public void searchable$setValidity(Optional<PatternSyntaxException> validityError) {
//		if (validityError.isPresent()) {
//			var error = validityError.get();
//
//			if (!Objects.equals(this.getTooltip(), this.validityErrorTooltip)) this.currentTooltip = this.getTooltip();
//			this.validityErrorTooltip = Tooltip.create(Text.literal(error.getMessage()).formatted(Formatting.RED), Text.literal(error.getDescription()));
//
//			this.setTooltip(this.validityErrorTooltip);
//		} else if (this.getTooltip() == this.validityErrorTooltip) {
//			this.setTooltip(this.currentTooltip);
//		}
//
		this.validityError = validityError;
	}

	@Override
	public Optional<PatternSyntaxException> searchable$getValidityError() {
		return this.validityError;
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
}
