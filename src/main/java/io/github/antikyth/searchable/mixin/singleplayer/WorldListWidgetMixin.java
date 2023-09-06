/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.singleplayer;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.util.MatchUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.world.storage.WorldSaveSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldListWidget.class)
public class WorldListWidgetMixin extends AlwaysSelectedEntryListWidget<WorldListWidget.AbstractWorldEntry> {
	@Unique
	private String query = "";

	@Unique
	private WorldSaveSummary lastSelection;

	public WorldListWidgetMixin(MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
		super(client, width, height, top, bottom, entryHeight);
	}

	// Re-select a world when it is shown again after re-filtering if no other world was selected.
	@ModifyArg(method = "filter(Ljava/lang/String;Ljava/util/List;)V", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/screen/world/WorldListWidget.addEntry (Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;)I"
	), index = 0)
	private EntryListWidget.Entry<WorldListWidget.AbstractWorldEntry> onAddEntry(EntryListWidget.Entry<WorldListWidget.AbstractWorldEntry> entry) {
		if (Searchable.config.reselectLastSelection && entry instanceof WorldListWidget.Entry worldEntry) {
			((SetQueryAccessor) (Object) worldEntry).searchable$setQuery(this.query);

			if (worldEntry.level == this.lastSelection) {
				this.setSelected((WorldListWidget.AbstractWorldEntry) entry);
			}
		}

		return entry;
	}

	// Match world details in search results as well as the display name and name.
	@ModifyReturnValue(method = "worldNameMatches", at = @At("RETURN"))
	private boolean matchWorldDetails(boolean matches, String query, WorldSaveSummary summary) {
		if (!Searchable.config.selectWorld.matchWorldDetails) return matches;

		return matches || MatchUtil.hasMatches(summary.getDetails(), query);
	}

	// Fix a vanilla bug: use `setSelected(null)`'s side effects to disable the world selection buttons.
	@Inject(method = "filter(Ljava/lang/String;Ljava/util/List;)V", at = @At("HEAD"))
	private void onFilter(String query, List<WorldSaveSummary> levels, CallbackInfo ci) {
		this.query = query;
		this.setSelected(null);
	}

	// Keep track of the last (non-null) selected world.
	@Inject(method = "setSelected(Lnet/minecraft/client/gui/screen/world/WorldListWidget$AbstractWorldEntry;)V", at = @At("TAIL"))
	public void onSetSelected(WorldListWidget.AbstractWorldEntry abstractWorldEntry, CallbackInfo ci) {
		if (Searchable.config.reselectLastSelection && abstractWorldEntry instanceof WorldListWidget.Entry entry) {
			this.lastSelection = entry.level;
		}
	}
}
