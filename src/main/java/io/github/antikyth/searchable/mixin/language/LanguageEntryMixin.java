package io.github.antikyth.searchable.mixin.language;

import io.github.antikyth.searchable.access.ILanguageEntryMixin;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry.class)
public abstract class LanguageEntryMixin extends AlwaysSelectedEntryListWidget.Entry<LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry> implements ILanguageEntryMixin {
	@Shadow
	public Text languageDefinition;

	@Unique
	private Style style;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConstructor(LanguageOptionsScreen.LanguageSelectionListWidget languageSelectionListWidget, String languageCode, LanguageDefinition languageDefinition, CallbackInfo ci) {
		this.style = this.languageDefinition.getStyle();
	}

	@Unique
	@Override
	public void searchable$highlightQuery(String query) {
		if (query == null || query.isEmpty()) {
			this.languageDefinition.setStyle(this.style);
		} else {
			String text = Formatting.strip(this.languageDefinition.getString());

			if (text != null) {
				int index = text.toLowerCase(Locale.ROOT).indexOf(query.toLowerCase(Locale.ROOT));

				if (index >= 0) {
					MutableText left = Text.literal(text.substring(0, index)).setStyle(this.style);
					MutableText highlight = Text.literal(text.substring(index, index + query.length())).setStyle(this.style);
					MutableText right = Text.literal(text.substring(index + query.length())).setStyle(this.style);

					this.languageDefinition = left.append(highlight.formatted(Formatting.UNDERLINE)).append(right);
				}
			}
		}
	}
}
