package io.github.antikyth.searchable.mixin.singleplayer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.antikyth.searchable.Util;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldListWidget.Entry.class)
public class WorldListWidget$EntryMixin implements SetQueryAccessor {
	@Final
	@Shadow
	public WorldSaveSummary level;

	@Unique
	private String query = "";

	@Unique
	private String worldDisplayName;
	@Unique
	private Text worldDisplayNameText;
	@Unique
	private Text worldDisplayNameWithHighlight;

	@Unique
	private String worldName;
	@Unique
	private Text worldNameText;
	@Unique
	private Text worldNameWithHighlight;

	@Unique
	private Text worldDetails;
	@Unique
	private Text worldDetailsWithHighlight;

	@Override
	public void searchable$setQuery(String query) {
		if (query != null && !query.equals(this.query)) {
			this.worldDisplayNameWithHighlight = (Text) Util.textWithHighlight(query, this.worldDisplayNameText);
			this.worldNameWithHighlight = (Text) Util.textWithHighlight(query, this.worldNameText);

			this.worldDetailsWithHighlight = (Text) Util.textWithHighlight(query, this.worldDetails);

			this.query = query;
		}
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConstructor(WorldListWidget worldListWidget, WorldListWidget levelList, WorldSaveSummary level, CallbackInfo ci) {
		// Used to check for changes to the display name.
		this.worldDisplayName = this.level.getDisplayName();
		// Used to update the highlight.
		this.worldDisplayNameText = Text.literal(this.worldDisplayName);
		this.worldDisplayNameWithHighlight = this.worldDisplayNameText;

		// Used to check for changes to the name.
		this.worldName = this.level.getName();
		// Used to update the highlight.
		this.worldNameText = Text.literal(this.worldName);
		this.worldNameWithHighlight = this.worldNameText;

		// Used to update the highlight.
		this.worldDetails = this.level.getDetails();
		this.worldDetailsWithHighlight = this.worldDetails;
	}

	@WrapOperation(method = "render", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
			ordinal = 0
	))
	private int drawWorldDisplayNameWithHighlight(GuiGraphics graphics, TextRenderer textRenderer, String worldDisplayName, int x, int y, int color, boolean shadowed, Operation<Integer> original) {
		if (worldDisplayName == null) return original.call(graphics, textRenderer, null, x, y, color, shadowed);

		// If the world display name has been changed, update the highlight first.
		if (!worldDisplayName.equals(this.worldDisplayName)) {
			this.worldDisplayName = worldDisplayName;
			this.worldDisplayNameText = Text.literal(this.worldDisplayName);

			this.worldDisplayNameWithHighlight = (Text) Util.textWithHighlight(this.query, this.worldDisplayNameText);
		}

		return graphics.drawText(textRenderer, this.worldDisplayNameWithHighlight, x, y, color, shadowed);
	}

	@WrapOperation(method = "render", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
			ordinal = 1
	))
	private int drawWorldNameWithHighlight(GuiGraphics graphics, TextRenderer textRenderer, String worldName, int x, int y, int color, boolean shadowed, Operation<Integer> original) {
		if (worldName == null) return original.call(graphics, textRenderer, null, x, y, color, shadowed);

		// If the world name has been changed, update the highlight first.
		if (!worldName.equals(this.worldName)) {
			this.worldName = worldName;
			this.worldNameText = Text.literal(this.worldName);

			this.worldNameWithHighlight = (Text) Util.textWithHighlight(this.query, this.worldNameText);
		}

		return graphics.drawText(textRenderer, this.worldNameWithHighlight, x, y, color, shadowed);
	}

	@ModifyArg(method = "render", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I",
			ordinal = 0
	))
	private Text drawWorldDetailsWithHighlight(Text worldDetails) {
		if (worldDetails == null) return null;

		if (!worldDetails.equals(this.worldDetails)) {
			this.worldDetails = worldDetails;

			this.worldDetailsWithHighlight = (Text) Util.textWithHighlight(this.query, this.worldDetails);
		}

		return this.worldDetailsWithHighlight;
	}
}
