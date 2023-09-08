package io.github.antikyth.searchable.mixin.singleplayer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.text.Text;
import net.minecraft.world.storage.WorldSaveSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldListWidget.Entry.class)
public class WorldListWidget$EntryMixin implements SetQueryAccessor {
	@Final
	@Shadow
	public WorldSaveSummary level;

	@Unique
	private String query = "";

	@Unique
	private final MatchManager worldDisplayNameMatchManager = new MatchManager();
	@Unique
	private final MatchManager worldNameMatchManager = new MatchManager();
	@Unique
	private final MatchManager worldDetailsMatchManager = new MatchManager();

	@Override
	public void searchable$setQuery(String query) {
		if (enabled() && query != null && !query.equals(this.query)) {
			this.query = query;
		}
	}

	@WrapOperation(method = "render", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
		ordinal = 0
	))
	private int drawWorldDisplayNameWithHighlight(GuiGraphics graphics, TextRenderer textRenderer, String worldDisplayName, int x, int y, int color, boolean shadowed, Operation<Integer> original) {
		if (enabled() && worldDisplayName != null) {
			return graphics.drawText(textRenderer, (Text) this.worldDisplayNameMatchManager.getHighlightedText(worldDisplayName, this.query), x, y, color, shadowed);
		}

		return original.call(graphics, textRenderer, worldDisplayName, x, y, color, shadowed);
	}

	@WrapOperation(method = "render", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
		ordinal = 1
	))
	private int drawWorldNameWithHighlight(GuiGraphics graphics, TextRenderer textRenderer, String worldName, int x, int y, int color, boolean shadowed, Operation<Integer> original) {
		if (enabled() && worldName != null) {
			// Get the matches from `this.level.getName()`, as while `worldName` is updated to add the date, it is not
			// checked in searches.
			var matches = this.worldNameMatchManager.getMatches(this.level.getName(), this.query);
			return graphics.drawText(textRenderer, (Text) this.worldNameMatchManager.getHighlightedText(worldName, matches), x, y, color, shadowed);
		}

		return original.call(graphics, textRenderer, worldName, x, y, color, shadowed);
	}

	@ModifyArg(method = "render", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I",
		ordinal = 0
	))
	private Text drawWorldDetailsWithHighlight(Text worldDetails) {
		if (enabled() && matchWorldDetails() && worldDetails != null) {
			return (Text) this.worldDetailsMatchManager.getHighlightedText(worldDetails, this.query);
		}

		return worldDetails;
	}

	@Unique
	private static boolean matchWorldDetails() {
		return Searchable.config.selectWorld.matchWorldDetails;
	}

	@Unique
	private static boolean enabled() {
		return Searchable.config.highlightMatches;
	}
}
