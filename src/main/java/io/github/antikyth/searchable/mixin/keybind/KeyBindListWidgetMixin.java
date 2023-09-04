/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.keybind;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.Util;
import io.github.antikyth.searchable.access.ISetQuery;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.KeyBindsScreen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.option.KeyBindListWidget;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Locale;

@Mixin(KeyBindListWidget.class)
public class KeyBindListWidgetMixin extends ElementListWidget<KeyBindListWidget.Entry> implements ISetQuery {
	@Unique
	private KeyBind[] keyBinds;
	@Unique
	private String query = "";

	@Unique
	@Override
	public void searchable$setQuery(String query) {
		if (!query.equals(this.query)) {
			this.filter(query);
			this.setScrollAmount(0.0);

			this.query = query;
		}
	}

	public KeyBindListWidgetMixin(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	@ModifyArg(method = "<init>", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/widget/ElementListWidget.<init> (Lnet/minecraft/client/MinecraftClient;IIIII)V"
	), index = 3)
	private static int adjustTopCoord(int top) {
		Searchable.LOGGER.debug("moving keybinds list down by 28px...");

		// 12 pixels lower to match the normal header height (32px), then 16 lower to make space for the search box.
		return top + 12 + 16;
	}

	@Inject(method = "<init>", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onConstructor(KeyBindsScreen parent, MinecraftClient client, CallbackInfo ci, KeyBind[] keyBinds) {
		this.keyBinds = keyBinds;
	}

	@Inject(method = "update", at = @At("TAIL"))
	public void onUpdate(CallbackInfo ci) {
		filter(this.query);
	}

	@Unique
	private void filter(String query) {
		this.clearEntries();

		String categoryMatch = null;
		String keyMatchCategory = null;

		for (KeyBind keyBind : this.keyBinds) {
			String category = keyBind.getCategory();

			if (category.equals(categoryMatch)) {
				// Whole category already matches.

				// Add all keys in category.
				this.addEntry(((KeyBindListWidget) (Object) this).new KeyBindEntry(keyBind, Text.translatable(keyBind.getTranslationKey())));
			} else if (matches(query, category)) {
				// New whole category match.
				categoryMatch = category;

				// Add category. Safe cast: input is Text, so output will be Text.
				this.addEntry(((KeyBindListWidget) (Object) this).new CategoryEntry((Text) Util.textWithHighlight(query, Text.translatable(category))));
				// Add key.
				this.addEntry(((KeyBindListWidget) (Object) this).new KeyBindEntry(keyBind, Text.translatable(keyBind.getTranslationKey())));
			} else {
				if (categoryMatch != null) {
					// End of whole category match.
					categoryMatch = null;
				}

				if (keyBindMatches(query, keyBind)) {
					// This key matches.

					if (!category.equals(keyMatchCategory)) {
						// This is a new category.
						keyMatchCategory = category;

						// Add category.
						this.addEntry(((KeyBindListWidget) (Object) this).new CategoryEntry(Text.translatable(category)));
					}

					var keyEntry = ((KeyBindListWidget) (Object) this).new KeyBindEntry(keyBind, Text.translatable(keyBind.getTranslationKey()));
					// Highlight the query within the key entry.
					((ISetQuery) keyEntry).searchable$setQuery(query);
					// Add key entry.
					this.addEntry(keyEntry);
				}
			}
		}
	}

	@Unique
	private static boolean keyBindMatches(String query, KeyBind keyBind) {
		return matches(query, keyBind.getTranslationKey()) || matches(query, keyBind.getKeyTranslationKey());
	}

	@Unique
	private static boolean matches(String query, String translationKey) {
		String lowercaseQuery = query.toLowerCase(Locale.ROOT);
		String target = I18n.translate(translationKey);

		return target != null && !target.isEmpty() && target.toLowerCase(Locale.ROOT).contains(lowercaseQuery);
	}
}
