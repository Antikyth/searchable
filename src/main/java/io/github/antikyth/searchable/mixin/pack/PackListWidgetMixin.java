/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.pack;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.config.SearchableConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PackListWidget.class)
public class PackListWidgetMixin extends AlwaysSelectedEntryListWidget<PackListWidget.ResourcePackEntry> {
	@Unique
	private static final int TITLE_Y = 8;
	@Unique
	private static final int TEXT_HEIGHT = 8;
	@Unique
	private static final int DROP_INFO_PADDING = 20 - (TITLE_Y + TEXT_HEIGHT);
	@Unique
	private static final int SHIFT = 16 + TEXT_HEIGHT + DROP_INFO_PADDING;

	public PackListWidgetMixin(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
		super(minecraftClient, width, height, top, bottom, entryHeight);
	}

	@ModifyArg(method = "<init>", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/widget/AlwaysSelectedEntryListWidget.<init> (Lnet/minecraft/client/MinecraftClient;IIIII)V",
		ordinal = 0
	), index = 3)
	private static int adjustTopCoord(int top) {
		if (!enabled()) return top;

		Searchable.LOGGER.debug("moving select pack screen pack list down by " + SHIFT + "px...");

		return top + SHIFT;
	}

	@Unique
	private static boolean enabled() {
		return SearchableConfig.INSTANCE.select_packs_screen.add_search.value();
	}
}
