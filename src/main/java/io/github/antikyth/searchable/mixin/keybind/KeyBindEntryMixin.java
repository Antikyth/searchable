package io.github.antikyth.searchable.mixin.keybind;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.GetMatchManagerAccessor;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.gui.widget.option.KeyBindListWidget;
import net.minecraft.client.option.KeyBind;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(KeyBindListWidget.KeyBindEntry.class)
public abstract class KeyBindEntryMixin extends KeyBindListWidget.Entry implements SetQueryAccessor {
	@Final
	@Shadow
	private KeyBind key;

	@Unique
	private String query = "";

	@Unique
	private final MatchManager bindButtonMatchManager = new MatchManager();

	@Unique
	@Override
	public void searchable$setQuery(String query) {
		if (!disabled() && query != null && !query.equals(this.query)) {
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
		if (disabled() || !Searchable.config.keybinds.matchBoundKey) return message;

		// safe cast: input is Text, output will be Text
		return (Text) bindButtonMatchManager.getHighlightedText(message, this.query);
	}

	// Render the highlighted binding name instead of `keyName`.
	@ModifyArg(method = "render", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I",
		ordinal = 0
	), index = 1)
	private Text renderBindNameWithHighlight(Text bindName) {
		if (disabled()) return bindName;

		return (Text) ((GetMatchManagerAccessor) this.key).searchable$getMatchManager().getHighlightedText(bindName, this.query);
	}

	@Unique
	private static boolean disabled() {
		return !Searchable.config.keybinds.enable || !Searchable.config.highlightMatches;
	}
}
