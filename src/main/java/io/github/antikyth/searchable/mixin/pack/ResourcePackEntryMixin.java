/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.pack;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.antikyth.searchable.accessor.MultilineTextLinesAccessor;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.config.SearchableConfig;
import io.github.antikyth.searchable.mixin.MultilineText$LineAccessor;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.OrderedText;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(PackListWidget.ResourcePackEntry.class)
public abstract class ResourcePackEntryMixin extends AlwaysSelectedEntryListWidget.Entry<PackListWidget.ResourcePackEntry> implements SetQueryAccessor {
	@Unique
	private String query = "";

	@Shadow
	@Final
	protected MinecraftClient client;

	@Shadow
	@Final
	private static final int DESCRIPTION_WIDTH = 0;

	// Match managers.
	@Unique
	private final MatchManager displayNameMatchManager = new MatchManager();
	@Unique
	private final MatchManager descriptionMatchManager = new MatchManager();


	@Override
	public void searchable$setQuery(String query) {
		if (enabled() && query != null && !query.equals(this.query)) {
			this.query = query;
		}
	}

	@ModifyExpressionValue(method = "render", at = @At(
		value = "FIELD",
		opcode = Opcodes.GETFIELD,
		target = "Lnet/minecraft/client/gui/screen/pack/PackListWidget$ResourcePackEntry;displayName:Lnet/minecraft/text/OrderedText;"
	))
	private OrderedText highlightDisplayName(OrderedText displayName) {
		if (displayName == null) return null;

		return this.displayNameMatchManager.getHighlightedText(displayName, this.query);
	}

	@ModifyExpressionValue(method = "render", at = @At(
		value = "FIELD",
		opcode = Opcodes.GETFIELD,
		target = "Lnet/minecraft/client/gui/screen/pack/PackListWidget$ResourcePackEntry;description:Lnet/minecraft/client/font/MultilineText;"
	))
	private MultilineText highlightDescription(MultilineText description) {
		if (description == null) return null;

		// If we can't get the lines of text then we can't highlight the text.
		if (!(description instanceof MultilineTextLinesAccessor multilineText)) return description;

		// Highlight the description.
		return createMultilineText(
			this.client,
			multilineText.getLines()
				.stream()
				// Highlight each line.
				.map(
					line -> this.descriptionMatchManager.getHighlightedText(
						((MultilineText$LineAccessor) line).getText(),
						this.query
					)
				)
				.toList()
		);
	}

	@Unique
	private static MultilineText createMultilineText(@NotNull MinecraftClient client, @NotNull List<OrderedText> lines) {
		return MultilineText.createMultiline(
			client.textRenderer,
			lines.stream()
				.map(orderedText -> MultilineText$LineAccessor.create(orderedText, DESCRIPTION_WIDTH)).toList()
		);
	}

	@Unique
	private static boolean enabled() {
		return SearchableConfig.INSTANCE.select_packs_screen.add_search.value()
			&& SearchableConfig.INSTANCE.highlight_matches.value();
	}
}
