package io.github.antikyth.searchable.mixin.singleplayer;

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
	private WorldSaveSummary lastSelection;

	public WorldListWidgetMixin(MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
		super(client, width, height, top, bottom, entryHeight);
	}

	// Re-select a world when it is shown again after re-filtering if no other world was selected.
	@ModifyArg(method = "filter(Ljava/lang/String;Ljava/util/List;)V", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/screen/world/WorldListWidget.addEntry (Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;)I"
	), index = 0)
	private EntryListWidget.Entry<WorldListWidget.AbstractWorldEntry> setSelectionIfEntryMatches(EntryListWidget.Entry<WorldListWidget.AbstractWorldEntry> entry) {
		if (entry instanceof WorldListWidget.Entry worldEntry && worldEntry.level == this.lastSelection) {
			this.setSelected((WorldListWidget.AbstractWorldEntry) entry);
		}

		return entry;
	}

	// Fix a vanilla bug: use `setSelected(null)`'s side effects to disable the world selection buttons.
	@Inject(method = "filter(Ljava/lang/String;Ljava/util/List;)V", at = @At("HEAD"))
	private void onFilter(String query, List<WorldSaveSummary> levels, CallbackInfo ci) {
		this.setSelected(null);
	}

	// Keep track of the last (non-null) selected world.
	@Inject(method = "setSelected(Lnet/minecraft/client/gui/screen/world/WorldListWidget$AbstractWorldEntry;)V", at = @At("TAIL"))
	public void onSetSelected(WorldListWidget.AbstractWorldEntry abstractWorldEntry, CallbackInfo ci) {
		if (abstractWorldEntry instanceof WorldListWidget.Entry entry) {
			this.lastSelection = entry.level;
		}
	}
}
