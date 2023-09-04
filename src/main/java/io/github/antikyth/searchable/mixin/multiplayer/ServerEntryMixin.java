package io.github.antikyth.searchable.mixin.multiplayer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
	private StringVisitable serverLabel;
	@Unique
	private StringVisitable serverLabelWithHighlight;

	@Unique
	@Override
	public void searchable$setQuery(String query) {
		if (query != null && !this.query.equals(query)) {
			// Safe cast: input is Text, so output will be Text.
			this.serverNameWithHighlight = (Text) Util.textWithHighlight(query, this.serverNameText);
			this.serverLabelWithHighlight = Util.textWithHighlight(query, this.serverLabel);

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

		// Used to check for changes to the label and to update the highlight.
		this.serverLabel = this.server.label;
		this.serverLabelWithHighlight = this.serverLabel;
	}

	/**
	 * Draw the server title text with the highlighted query match, if any.
	 */
	@WrapOperation(method = "render", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
			ordinal = 0
	))
	private int drawServerNameWithHighlight(GuiGraphics graphics, TextRenderer textRenderer, String serverName, int x, int y, int color, boolean shadowed, Operation<Integer> original) {
		if (serverName == null) return original.call(graphics, textRenderer, null, x, y, color, shadowed);

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
	 */
	@ModifyArg(method = "render", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/font/TextRenderer.wrapLines (Lnet/minecraft/text/StringVisitable;I)Ljava/util/List;",
			ordinal = 0
	))
	private StringVisitable drawServerLabelWithHighlight(StringVisitable label) {
		if (this.serverLabel == null) return null;

		// If the server label has been changed, update the highlight first.
		if (!this.serverLabel.equals(label)) {
			this.serverLabel = label;
			this.serverLabelWithHighlight = Util.textWithHighlight(this.query, this.serverLabel);
		}

		return this.serverLabelWithHighlight;
	}
}
