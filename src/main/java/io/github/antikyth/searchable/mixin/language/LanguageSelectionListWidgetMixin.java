/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.language;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.GetSearchBoxAccessor;
import io.github.antikyth.searchable.accessor.language.LanguageEntryAccessor;
import io.github.antikyth.searchable.accessor.language.LanguageSelectionListWidgetAccessor;
import io.github.antikyth.searchable.mixin.EntryListWidgetMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen.LanguageSelectionListWidget;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;
import java.util.Map;

@ClientOnly
@Mixin(LanguageOptionsScreen.LanguageSelectionListWidget.class)
public abstract class LanguageSelectionListWidgetMixin<E extends EntryListWidget.Entry<E>> extends EntryListWidgetMixin<E> implements LanguageSelectionListWidgetAccessor {
	/**
	 * The last selected language entry. Used so that if the entry is hidden and later shown, it can be re-selected.
	 */
	@Unique
	@Nullable
	public LanguageEntry selectedLanguage;

	@Unique
	@Nullable
	@Override
	public LanguageEntry searchable$getSelectedLanguage() {
		return selectedLanguage;
	}

	/**
	 * The previous query. Used to check whether the list needs to be re-filtered.
	 */
	@Unique
	private String query = "";

	// Move the top of the language selection list down by 16 pixels to make space for the search box.
	@ModifyArg(method = "<init>", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/widget/AlwaysSelectedEntryListWidget;<init>(Lnet/minecraft/client/MinecraftClient;IIIII)V"
	), index = 3)
	private static int adjustTopCoord(int top) {
		if (!enabled()) return top;

		Searchable.LOGGER.debug("moving language selection list down by 16px...");

		// The world selection screen has the top of its selection list 16 pixels lower to make space for its search
		// box.
		return top + 16;
	}

	// Filter the language selection list at the end of the constructor.
	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConstructor(LanguageOptionsScreen languageOptionsScreen, MinecraftClient client, CallbackInfo ci) {
		if (!enabled()) return;

		String query = ((GetSearchBoxAccessor) languageOptionsScreen).searchable$getSearchBox().getText();
		this.searchable$filter(query, languageOptionsScreen.languageManager.getAllLanguages());
	}

//	// Use `EntryListWidget.nextFocusPath` instead of `AlwaysSelectedEntryListWidget`'s so that we can hide the selected
//	// entry when needed.
//	//
//	// Yes, this is overwriting the method. It's because this isn't the behavior we want, nor any potential
//	// modifications made to it by other mixins (which I doubt anyone does).
//	@Nullable
//	public ElementPath nextFocusPath(GuiNavigationEvent event) {
//		return ((EntryListWidget<?>) (Object) this).nextFocusPath(event);
//	}

	// Keep track of the latest selected language entry so it can be re-selected if a query hides it and it is then
	// later shown again.
	@Override
	protected void onSetSelected(@Nullable E entry, CallbackInfo ci) {
		if (!enabled()) return;

		if (Searchable.config.reselectLastSelection && entry != null && !entry.equals(selectedLanguage)) {
			Searchable.LOGGER.debug("updating selected language...");

			selectedLanguage = (LanguageEntry) entry;
		}
	}

	/**
	 * Filters the language selection list by the given query.
	 *
	 * @param languages The language source (i.e. `languageManager.getAllLanguages()`)
	 */
	@Override
	@Unique
	@SuppressWarnings("unchecked")
	public void searchable$filter(String query, Map<String, LanguageDefinition> languages) {
		String lowercaseQuery = query.toLowerCase(Locale.ROOT);

		// If the query has changed...
		if (!lowercaseQuery.equals(this.query)) {
			Searchable.LOGGER.debug("filtering language selection list by query \"" + lowercaseQuery + "\"...");

			this.clearEntries();

			languages.forEach((code, definition) -> {
				// Add each entry matching the query back
				if (this.languageMatches(lowercaseQuery, definition)) {
					var entry = ((LanguageSelectionListWidget) (Object) this).new LanguageEntry(code, definition);
					((LanguageEntryAccessor) entry).searchable$highlightQuery(query);

					this.addEntry((E) entry);

					// If it's the previously selected language, select it again.
					if (Searchable.config.reselectLastSelection && selectedLanguage != null && code.equals(selectedLanguage.languageCode)) {
						this.setSelected((E) entry);
					}
				}
			});

			// After filtering, set the scroll to be centered on the selected entry if there is one, or otherwise at the
			// top.
			if (this.getSelectedOrNull() != null) {
				this.centerScrollOn(this.getSelectedOrNull());
			} else {
				this.setScrollAmount(0.0);
			}
		}

		this.query = lowercaseQuery;
	}

	/**
	 * Whether the given language matches the given query.
	 */
	@Unique
	private boolean languageMatches(String lowercaseQuery, LanguageDefinition language) {
		var string = Formatting.strip(language.getDisplayText().getString());

		return string != null && !string.isEmpty() && string.toLowerCase(Locale.ROOT).contains(lowercaseQuery);
	}

	@Unique
	private static boolean enabled() {
		return Searchable.config.language.enable;
	}
}
