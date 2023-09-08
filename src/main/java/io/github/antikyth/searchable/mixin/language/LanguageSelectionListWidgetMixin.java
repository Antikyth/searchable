/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.language;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.MatchesAccessor;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.accessor.language.LanguageSelectionListWidgetAccessor;
import io.github.antikyth.searchable.mixin.EntryListWidgetMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen.LanguageSelectionListWidget;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ClientOnly
@Mixin(LanguageOptionsScreen.LanguageSelectionListWidget.class)
public abstract class LanguageSelectionListWidgetMixin<E extends EntryListWidget.Entry<E>> extends EntryListWidgetMixin<E> implements LanguageSelectionListWidgetAccessor {
	@Shadow
	protected abstract void method_48261(String languageCode, String selectedLanguageCode, LanguageDefinition definition);

	/**
	 * The last selected language entry. Used so that if the entry is hidden and later shown, it can be re-selected.
	 */
	@Unique
	@Nullable
	public LanguageEntry selectedLanguage;

	@Unique
	private LanguageOptionsScreen parent;

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

	@Override
	public void searchable$setQuery(String query) {
		if (!disabled() && query != null && !query.equals(this.query)) {
			this.query = query;
			this.filter();
		}
	}

	// Move the top of the language selection list down by 16 pixels to make space for the search box.
	@ModifyArg(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/gui/widget/AlwaysSelectedEntryListWidget;<init>(Lnet/minecraft/client/MinecraftClient;IIIII)V"
	), index = 3)
	private static int adjustTopCoord(int top) {
		if (disabled()) return top;

		Searchable.LOGGER.debug("moving language selection list down by 16px...");

		// The world selection screen has the top of its selection list 16 pixels lower to make space for its search
		// box.
		return top + 16;
	}

	// Filter the language selection list at the end of the constructor.
	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConstructor(LanguageOptionsScreen languageOptionsScreen, MinecraftClient client, CallbackInfo ci) {
		if (disabled()) return;

		this.parent = languageOptionsScreen;
	}

	// Keep track of the latest selected language entry, so it can be re-selected if a query hides it and it is then
	// later shown again.
	@Override
	protected void onSetSelected(@Nullable E entry, CallbackInfo ci) {
		if (disabled()) return;

		if (Searchable.config.reselectLastSelection && entry != null && !entry.equals(selectedLanguage)) {
			Searchable.LOGGER.debug("updating selected language...");

			selectedLanguage = (LanguageEntry) entry;
		}
	}

	@Unique
	private void filter() {
		this.clearEntries();

		String selectedLanguageCode = Searchable.config.reselectLastSelection && this.selectedLanguage != null
			? this.selectedLanguage.languageCode : this.parent.languageManager.getLanguage();

		this.parent.languageManager.getAllLanguages()
			.forEach((languageCode, languageDefinition) -> this.method_48261(selectedLanguageCode, languageCode, languageDefinition));

		if (this.getSelectedOrNull() != null) {
			this.centerScrollOn(this.getSelectedOrNull());
		} else {
			this.setScrollAmount(0.0);
		}
	}

	@WrapWithCondition(method = "method_48261", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/screen/option/LanguageOptionsScreen$LanguageSelectionListWidget.addEntry (Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;)I",
		ordinal = 0
	))
	private boolean filterLanguage(LanguageSelectionListWidget instance, EntryListWidget.Entry<LanguageEntry> entry) {
		if (entry instanceof LanguageEntry languageEntry) {
			if (((MatchesAccessor) languageEntry).searchable$matches(this.query)) {
				((SetQueryAccessor) languageEntry).searchable$setQuery(this.query);

				return true;
			}

			return false;
		}

		return true;
	}

//	@Override
//	@Unique
//	@SuppressWarnings("unchecked")
//	public void searchable$filter(String query, Map<String, LanguageDefinition> languages) {
//		// If the query has changed...
//		if (query != null && !query.equals(this.query)) {
//			Searchable.LOGGER.debug("filtering language selection list by query \"" + query + "\"...");
//
//			this.clearEntries();
//
//			languages.forEach((code, definition) -> {
//				// Add each entry matching the query back
//				if (this.languageMatches(query, definition)) {
//					var entry = ((LanguageSelectionListWidget) (Object) this).new LanguageEntry(code, definition);
//
//					((SetQueryAccessor) entry).searchable$setQuery(query);
//
//					this.addEntry((E) entry);
//
//					// If it's the previously selected language, select it again.
//					if (Searchable.config.reselectLastSelection && selectedLanguage != null && code.equals(selectedLanguage.languageCode)) {
//						this.setSelected((E) entry);
//					}
//				}
//			});
//
//			// After filtering, set the scroll to be centered on the selected entry if there is one, or otherwise at the
//			// top.
//			if (this.getSelectedOrNull() != null) {
//				this.centerScrollOn(this.getSelectedOrNull());
//			} else {
//				this.setScrollAmount(0.0);
//			}
//
//			this.query = query;
//		}
//	}
//
//	/**
//	 * Whether the given language matches the given query.
//	 */
//	@Unique
//	private boolean languageMatches(String query, LanguageDefinition language) {
//		return MatchManager.hasMatches(language.getDisplayText(), query);
//	}

	@Unique
	private static boolean disabled() {
		return !Searchable.config.language.enable;
	}
}
