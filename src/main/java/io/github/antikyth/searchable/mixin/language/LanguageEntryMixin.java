package io.github.antikyth.searchable.mixin.language;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.language.LanguageEntryAccessor;
import io.github.antikyth.searchable.util.MatchUtil;
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
public abstract class LanguageEntryMixin extends AlwaysSelectedEntryListWidget.Entry<LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry> implements LanguageEntryAccessor {
	@Shadow
	public Text languageDefinition;

	@Unique
	private Text textWithHighlight;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConstructor(LanguageOptionsScreen.LanguageSelectionListWidget languageSelectionListWidget, String languageCode, LanguageDefinition languageDefinition, CallbackInfo ci) {
		if (disabled()) return;

		this.textWithHighlight = this.languageDefinition;
	}

	@ModifyArg(method = "render", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/GuiGraphics.drawCenteredShadowedText (Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V",
			ordinal = 0
	), index = 1)
	private Text renderLanguageWithHighlight(Text languageDefinition) {
		if (disabled()) return languageDefinition;

		return this.textWithHighlight;
	}

	@Unique
	@Override
	public void searchable$highlightQuery(String query) {
		if (disabled()) return;

		// Safe cast: input is Text, so output will be Text.
		this.textWithHighlight = (Text) MatchUtil.getHighlightedText(this.languageDefinition, query);
	}

	@Unique
	private boolean disabled() {
		return !Searchable.config.language.enable || !Searchable.config.highlightMatches;
	}
}
