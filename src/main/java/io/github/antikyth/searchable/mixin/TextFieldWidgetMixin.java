package io.github.antikyth.searchable.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.antikyth.searchable.accessor.TextFieldWidgetValidityAccessor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Formatting;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextFieldWidget.class)
public class TextFieldWidgetMixin implements TextFieldWidgetValidityAccessor {
	@Unique
	private static final int INVALID_COLOR;

	static {
		Integer invalidColor = Formatting.RED.getColorValue();

		INVALID_COLOR = invalidColor == null ? TextFieldWidget.DEFAULT_EDITABLE_COLOR : invalidColor;
	}

	@Unique
	private boolean valid = true;

	@Override
	public void searchable$setValidity(boolean valid) {
		this.valid = valid;
	}

	@Override
	public boolean searchable$getValidity() {
		return this.valid;
	}

	@ModifyExpressionValue(method = "drawWidget", at = @At(
		value = "FIELD",
		target = "net/minecraft/client/gui/widget/TextFieldWidget.editableColor : I",
		opcode = Opcodes.GETFIELD,
		ordinal = 0
	))
	private int drawTextWithInvalidColor(int color) {
		return this.valid ? color : INVALID_COLOR;
	}
}
