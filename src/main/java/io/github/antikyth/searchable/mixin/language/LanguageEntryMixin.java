package io.github.antikyth.searchable.mixin.language;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry.class)
public abstract class LanguageEntryMixin extends AlwaysSelectedEntryListWidget.Entry<LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry> implements SetQueryAccessor {
	@Unique
	private final MatchManager matchManager = new MatchManager();
	@Unique
	private String query = "";

	@Override
	public void searchable$setQuery(String query) {
		if (!disabled() && query != null) {
			this.query = query;
		}
	}

	@ModifyArg(method = "render", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/GuiGraphics.drawCenteredShadowedText (Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V",
			ordinal = 0
	), index = 1)
	private Text renderLanguageWithHighlight(Text languageDefinition) {
		if (disabled()) return languageDefinition;

		return (Text) this.matchManager.getHighlightedText(languageDefinition, this.query);
	}

	@Unique
	private boolean disabled() {
		return !Searchable.config.language.enable || !Searchable.config.highlightMatches;
	}
}
