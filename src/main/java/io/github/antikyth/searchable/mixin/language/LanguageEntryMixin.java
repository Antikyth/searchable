package io.github.antikyth.searchable.mixin.language;

import io.github.antikyth.searchable.accessor.MatchesAccessor;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.config.SearchableConfig;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry.class)
public abstract class LanguageEntryMixin extends AlwaysSelectedEntryListWidget.Entry<LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry> implements SetQueryAccessor, MatchesAccessor {
	@Shadow
	public Text languageDefinition;

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

	@Unique
	private Text languageText;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onConstructor(LanguageOptionsScreen.LanguageSelectionListWidget languageSelectionListWidget, String languageCode, LanguageDefinition languageDefinition, CallbackInfo ci) {
		this.languageText = this.languageDefinition;
	}

	@ModifyArg(method = "render", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/GuiGraphics.drawCenteredShadowedText (Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V",
		ordinal = 0
	), index = 1)
	private Text renderLanguageWithHighlight(Text languageDefinition) {
		if (languageDefinition != null && !languageDefinition.equals(this.languageText)) {
			this.languageText = languageDefinition;
		}

		if (disabled() || languageDefinition == null) return languageDefinition;

		return (Text) this.matchManager.getHighlightedText(this.languageText, this.query);
	}

	@Override
	public boolean searchable$matches(String query) {
		return this.matchManager.hasMatches(this.languageText, query);
	}

	@Unique
	private boolean disabled() {
		return !SearchableConfig.INSTANCE.language_screen.add_search.value() || !SearchableConfig.INSTANCE.highlight_matches.value();
	}
}
