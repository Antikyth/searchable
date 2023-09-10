/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.keybind;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.MatchesAccessor;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.util.Pair;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.KeyBindsScreen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.option.KeyBindListWidget;
import net.minecraft.client.option.KeyBind;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mixin(KeyBindListWidget.class)
public class KeyBindListWidgetMixin extends ElementListWidget<KeyBindListWidget.Entry> implements SetQueryAccessor {
	@Unique
	private final Map<String, Pair<MatchManager, List<KeyBind>>> map = new LinkedHashMap<>();

	@Unique
	private String query = "";

	@Unique
	@Override
	public void searchable$setQuery(String query) {
		if (enabled() && query != null && !query.equals(this.query)) {
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
		if (!enabled()) return top;

		Searchable.LOGGER.debug("moving keybinds list down by 28px...");

		// 12 pixels lower to match the normal header height (32px), then 16 lower to make space for the search box.
		return top + 12 + 16;
	}

	@Inject(method = "<init>", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onConstructor(KeyBindsScreen parent, MinecraftClient client, CallbackInfo ci, KeyBind[] keyBinds) {
		if (!enabled()) return;

		for (KeyBind keyBind : keyBinds) {
			// Store the key binds in a map from their category to that category's `MatchManager` and the key binds in it.
			this.map.computeIfAbsent(keyBind.getCategory(), category -> new Pair<>(new MatchManager(), new ArrayList<>())).second.add(keyBind);
		}
	}

	@Inject(method = "update", at = @At("TAIL"))
	public void onUpdate(CallbackInfo ci) {
		if (!enabled()) return;

		filter(this.query);
	}

	@Unique
	private void filter(String query) {
		this.map.forEach((category, pair) -> {
			var categoryTranslation = Text.translatable(category);

			// If the category matches the query...
			if (pair.first.hasMatches(categoryTranslation, query)) {
				// Add the category.
				this.addCategoryEntry((Text) pair.first.getHighlightedText(categoryTranslation, query));

				// Add all its key binds.
				for (KeyBind keyBind : pair.second) {
					this.addKeyEntry(keyBind, null);
				}
			} else {
				boolean addedCategory = false;

				for (KeyBind keyBind : pair.second) {
					// If the key bind matches the query...
					if (((MatchesAccessor) keyBind).searchable$matches(query)) {
						// If its category hasn't been added, add the category.
						if (!addedCategory) {
							this.addEntry(((KeyBindListWidget) (Object) this).new CategoryEntry(categoryTranslation));
							addedCategory = true;
						}

						// Add the key bind.
						this.addKeyEntry(keyBind, query);
					}
				}
			}
		});
	}

	@Unique
	private void addCategoryEntry(Text title) {
		this.addEntry(((KeyBindListWidget) (Object) this).new CategoryEntry(title));
	}

	@Unique
	private void addKeyEntry(KeyBind keyBind, @Nullable String query) {
		Text keyBindTranslation = Text.translatable(keyBind.getTranslationKey());
		var entry = ((KeyBindListWidget) (Object) this).new KeyBindEntry(keyBind, keyBindTranslation);

		if (query != null) ((SetQueryAccessor) entry).searchable$setQuery(query);

		this.addEntry(entry);
	}

	@Unique
	private static boolean enabled() {
		return Searchable.config.keybinds.enable;
	}
}
