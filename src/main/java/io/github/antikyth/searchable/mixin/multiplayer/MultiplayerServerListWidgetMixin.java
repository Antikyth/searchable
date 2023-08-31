/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin.multiplayer;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import io.github.antikyth.searchable.access.IMultiplayerServerListWidgetMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget.LanServerEntry;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget.ServerEntry;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;

@Mixin(MultiplayerServerListWidget.class)
public abstract class MultiplayerServerListWidgetMixin<E extends AlwaysSelectedEntryListWidget.Entry<E>> extends AlwaysSelectedEntryListWidget<E> implements IMultiplayerServerListWidgetMixin {
	@Unique
	private String query = "";

	// Mixin will ignore this - required because of extending `AlwaysSelectedEntryListWidget`
	public MultiplayerServerListWidgetMixin(MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
		super(client, width, height, top, bottom, entryHeight);
	}

	@Unique
	@Override
	public String getQuery() {
		return this.query;
	}

	@Unique
	@Override
	public void setQuery(String query) {
		if (!query.equals(this.query)) {
			this.filter(query);
		}

		this.query = query;
	}

	@Final
	@Shadow
	private MultiplayerScreen screen;

	@Final
	@Shadow
	private List<ServerEntry> servers;
	@Final
	@Shadow
	private List<LanServerEntry> lanServers;
	@Final
	@Shadow
	private MultiplayerServerListWidget.Entry scanningEntry;

	@Shadow
	protected abstract void updateEntries();

	@Unique
	private MultiplayerServerListWidget.Entry lastSelection;

	@Inject(method = "setSelected(Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget$Entry;)V", at = @At("TAIL"))
	public void onSetSelected(@Nullable MultiplayerServerListWidget.Entry entry, CallbackInfo ci) {
		if (entry != null) {
			lastSelection = entry;
		}
	}

	// Filters very similarly to `updateEntries`, but as it isn't run during initialization, it can use `setSelected` to
	// maintain the selection when searching.
	@Unique
	@SuppressWarnings("unchecked")
	private void filter(String query) {
		this.clearEntries();

		this.servers.forEach(entry -> {
			if (serverMatchesQuery(query, entry)) {
				this.addEntry((E) entry);

				if (entry == lastSelection) {
					this.setSelected((E) entry);
				}
			}
		});
		this.addEntry((E) this.scanningEntry);
		this.lanServers.forEach(entry -> {
			if (lanServerMatchesQuery(query, entry)) {
				this.addEntry((E) entry);

				if (entry == lastSelection) {
					this.setSelected((E) entry);
				}
			}
		});

		this.screen.updateButtonActivationStates();

		if (this.getSelectedOrNull() != null) {
			this.centerScrollOn(this.getSelectedOrNull());
		} else {
			this.setScrollAmount(0.0);
		}
	}

	// Filter the server entries added back when `updateEntries()` is called.
	@WrapWithCondition(method = "method_36889", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget.addEntry (Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;)I"
	))
	private static boolean filterServerEntry(MultiplayerServerListWidget instance, EntryListWidget.Entry<MultiplayerServerListWidget.Entry> entry) {
		return serverMatchesQuery(((IMultiplayerServerListWidgetMixin) instance).getQuery(), (ServerEntry) entry);
	}

	// Filter the LAN server entries added back when `updateEntries()` is called.
	@WrapWithCondition(method = "method_36888", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget.addEntry (Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;)I"
	))
	private static boolean filterLanServerEntry(MultiplayerServerListWidget instance, EntryListWidget.Entry<MultiplayerServerListWidget.Entry> entry) {
		return lanServerMatchesQuery(((IMultiplayerServerListWidgetMixin) instance).getQuery(), (LanServerEntry) entry);
	}

	@Unique
	private static boolean serverMatchesQuery(String query, ServerEntry entry) {
		String name = entry.getServer().name;

		Text label = entry.getServer().label;
		String labelString = label == null ? null : Formatting.strip(label.getString());

		return matchesQuery(query, name, labelString);
	}

	@Unique
	private static boolean lanServerMatchesQuery(String query, LanServerEntry entry) {
		String title = I18n.translate("lanServer.title");
		String motd = Formatting.strip(entry.getLanServerEntry().getMotd());

		return matchesQuery(query, title, motd);
	}

	@Unique
	private static boolean matchesQuery(String query, @Nullable String title, @Nullable String motd) {
		String lowercaseQuery = query.toLowerCase(Locale.ROOT);

		boolean titleMatches = title != null && !title.isEmpty() && title.toLowerCase(Locale.ROOT).contains(lowercaseQuery);
		boolean motdMatches = motd != null && !motd.isEmpty() && motd.toLowerCase(Locale.ROOT).contains(lowercaseQuery);

		return titleMatches || motdMatches;
	}
}
