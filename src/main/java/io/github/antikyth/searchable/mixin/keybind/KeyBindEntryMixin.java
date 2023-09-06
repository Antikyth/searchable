package io.github.antikyth.searchable.mixin.keybind;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.util.MatchUtil;
import net.minecraft.client.gui.widget.option.KeyBindListWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindListWidget.KeyBindEntry.class)
public abstract class KeyBindEntryMixin extends KeyBindListWidget.Entry implements SetQueryAccessor {
	@Final
	@Shadow
	private Text keyName;

	@Unique
	private String query = "";
	@Unique
	private Text bindNameWithHighlight;

	@Unique
	@Override
	public void searchable$setQuery(String query) {
		if (enabled() && query != null && !query.equals(this.query)) {
			this.query = query;
			this.update();
		}
	}

	// Highlight the bind button's text if it matches the query.
	@ModifyArg(method = "update", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/widget/ButtonWidget.setMessage (Lnet/minecraft/text/Text;)V",
			ordinal = 0
	), index = 0)
	private Text highlightBindButtonText(Text message) {
		if (!enabled() || !Searchable.config.keybinds.matchBoundKey) return message;

		// safe cast: input is Text, output will be Text
		return (Text) MatchUtil.getHighlightedText(message, this.query);
	}

	// Update the highlight for the binding name.
	@Inject(method = "update", at = @At("HEAD"))
	protected void onUpdate(CallbackInfo ci) {
		if (!enabled()) return;

		// safe cast: input is Text, output will be Text
		this.bindNameWithHighlight = (Text) MatchUtil.getHighlightedText(this.keyName, this.query);
	}

	// Render the highlighted binding name instead of `keyName`.
	@ModifyArg(method = "render", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I",
			ordinal = 0
	), index = 1)
	private Text renderBindNameWithHighlight(Text keyName) {
		if (!enabled()) return keyName;

		return this.bindNameWithHighlight;
	}

	@Unique
	private static boolean enabled() {
		return Searchable.config.keybinds.enable && Searchable.config.highlightMatches;
	}
}
