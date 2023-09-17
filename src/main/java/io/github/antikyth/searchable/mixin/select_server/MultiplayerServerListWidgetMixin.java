/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.select_server;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import io.github.antikyth.searchable.accessor.MatchesAccessor;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.accessor.multiplayer.MultiplayerServerListWidgetAccessor;
import io.github.antikyth.searchable.config.SearchableConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget.LanServerEntry;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget.ServerEntry;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerServerListWidget.class)
public abstract class MultiplayerServerListWidgetMixin<E extends AlwaysSelectedEntryListWidget.Entry<E>> extends AlwaysSelectedEntryListWidget<E> implements MultiplayerServerListWidgetAccessor {
	@Final
	@Shadow
	private MultiplayerScreen screen;

	@Shadow
	protected abstract void updateEntries();

	@Unique
	private MultiplayerServerListWidget.Entry lastSelection;

	@Override
	public MultiplayerServerListWidget.Entry searchable$getLastSelection() {
		return this.lastSelection;
	}

	@Unique
	private String query = "";

	@Unique
	@Override
	public void searchable$setQuery(String query) {
		if (enabled() && query != null && !query.equals(this.query)) {
			this.query = query;

			this.updateEntries();
			this.screen.updateButtonActivationStates();

			if (this.getSelectedOrNull() != null) {
				this.centerScrollOn(this.getSelectedOrNull());
			} else {
				this.setScrollAmount(0.0);
			}
		}
	}

	// Mixin will ignore this - required because of extending `AlwaysSelectedEntryListWidget`
	public MultiplayerServerListWidgetMixin(MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
		super(client, width, height, top, bottom, entryHeight);
	}

	@Inject(method = "setSelected(Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget$Entry;)V", at = @At("TAIL"))
	public void onSetSelected(@Nullable MultiplayerServerListWidget.Entry entry, CallbackInfo ci) {
		if (!enabled()) return;

		if (reselectLastSelection() && entry != null) {
			lastSelection = entry;
		}
	}

	// Filter the server entries added back when `updateEntries()` is called.
	@WrapWithCondition(method = "method_36889", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget.addEntry (Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;)I"
	))
	private static boolean filterServerEntry(MultiplayerServerListWidget instance, EntryListWidget.Entry<MultiplayerServerListWidget.Entry> entry) {
		var self = (MultiplayerServerListWidgetMixin<?>) (Object) instance;

		if (enabled() && entry instanceof ServerEntry serverEntry) {
			// No need to close, this is just comparing the reference, it is then closed in the target class.
			if (reselectLastSelection() && serverEntry == self.lastSelection) {
				instance.setSelected(serverEntry);
			}

			((SetQueryAccessor) serverEntry).searchable$setQuery(self.query);

			return ((MatchesAccessor) serverEntry).searchable$matches(self.query);
		}

		return true;
	}

	// Filter the LAN server entries added back when `updateEntries()` is called.
	@WrapWithCondition(method = "method_36888", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget.addEntry (Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;)I"
	))
	private static boolean filterLanServerEntry(MultiplayerServerListWidget instance, EntryListWidget.Entry<MultiplayerServerListWidget.Entry> entry) {
		var self = (MultiplayerServerListWidgetMixin<?>) (Object) instance;

		if (enabled() && entry instanceof LanServerEntry lanServerEntry) {
			// No need to close, this is just comparing the reference, it is then closed in the target class.
			if (reselectLastSelection() && lanServerEntry == self.lastSelection) {
				instance.setSelected(lanServerEntry);
			}

			((SetQueryAccessor) lanServerEntry).searchable$setQuery(self.query);

			return ((MatchesAccessor) lanServerEntry).searchable$matches(self.query);
		}

		return true;
	}

	@Unique
	private static boolean reselectLastSelection() {
		return SearchableConfig.INSTANCE.reselect_last_selection.value();
	}

	@Unique
	private static boolean enabled() {
		return SearchableConfig.INSTANCE.select_server_screen.add_search.value();
	}
}
