package io.github.antikyth.searchable.mixin.multiplayer;

import io.github.antikyth.searchable.Util;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class ServerEntryMixin implements SetQueryAccessor {
	@Final
	@Shadow
	private ServerInfo server;

	@Unique
	private String query = "";

	@Unique
	private String serverName;
	@Unique
	private Text serverNameText;
	@Unique
	private Text serverNameWithHighlight;

	@Unique
	@Override
	public void searchable$setQuery(String query) {
		if (query != null && !this.query.equals(query)) {
			// Safe cast: input is Text, so output will be Text.
			this.serverNameWithHighlight = (Text) Util.textWithHighlight(query, this.serverNameText);
			this.query = query;
		}
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	protected void onConstructor(MultiplayerServerListWidget multiplayerServerListWidget, MultiplayerScreen screen, ServerInfo server, CallbackInfo ci) {
		// Used to check for changes to the name.
		this.serverName = this.server.name;
		// Used to update the highlight.
		this.serverNameText = Text.literal(this.serverName);
		this.serverNameWithHighlight = this.serverNameText;
	}

	/**
	 * Draw the server title text with the highlighted query match, if any.
	 * <p>
	 * Redirect is necessary as the `drawText` method used takes a `String` instead of `Text`.
	 */
	@Redirect(method = "render", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
			ordinal = 0
	))
	private int drawServerNameWithHighlight(GuiGraphics graphics, TextRenderer textRenderer, String serverName, int x, int y, int color, boolean shadowed) {
		if (serverName == null) return graphics.drawText(textRenderer, (String) null, x, y, color, shadowed);

		// If the server name has been changed, update the highlight first.
		if (!this.serverName.equals(serverName)) {
			this.serverName = serverName;
			this.serverNameText = Text.literal(this.serverName);

			this.serverNameWithHighlight = (Text) Util.textWithHighlight(this.query, this.serverNameText);
		}

		return graphics.drawText(textRenderer, this.serverNameWithHighlight, x, y, color, shadowed);
	}

	/**
	 * Highlight the label used in the {@link MultiplayerServerListWidget.ServerEntry#render render} method with the
	 * query match, if any.
	 * <p>
	 * This is run every render, re-creating the highlight every single time, cloning the text every single time. As far
	 * as I can tell, there is no reliable way around that: we don't know when the server's label is updated.
	 * {@link Text} doesn't seem to implement {@link Object#equals} other than reference equality. Even if we checked if
	 * the string had changed, (a) I don't think that would be worse than re-calculating this highlight, as it also
	 * clones the text, (b) it wouldn't take into account changes in formatting.
	 */
	@ModifyArg(method = "render", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/font/TextRenderer.wrapLines (Lnet/minecraft/text/StringVisitable;I)Ljava/util/List;",
			ordinal = 0
	))
	private StringVisitable drawServerLabelWithHighlight(StringVisitable label) {
		return Util.textWithHighlight(this.query, label);
	}
}
