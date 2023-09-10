package io.github.antikyth.searchable.mixin.multiplayer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.antikyth.searchable.accessor.MatchesAccessor;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.config.SearchableConfig;
import io.github.antikyth.searchable.util.match.MatchManager;
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
public class ServerEntryMixin implements SetQueryAccessor, MatchesAccessor {
	@Final
	@Shadow
	private ServerInfo server;

	@Unique
	private String query = "";


	@Unique
	private String serverName;
	@Unique
	private Text serverLabel;

	@Unique
	private final MatchManager serverNameMatchManager = new MatchManager();
	@Unique
	private final MatchManager serverLabelMatchManager = new MatchManager();

	@Unique
	@Override
	public void searchable$setQuery(String query) {
		if (enabled() && query != null && !query.equals(this.query)) {
			this.query = query;
		}
	}

	@Override
	public boolean searchable$matches(String query) {
		if (this.serverNameMatchManager.hasMatches(this.serverName, query)) return true;

		if (SearchableConfig.INSTANCE.select_server_screen.match_motds.value())
			return this.serverLabelMatchManager.hasMatches(this.serverLabel, query);

		return false;
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	protected void onConstructor(MultiplayerServerListWidget multiplayerServerListWidget, MultiplayerScreen screen, ServerInfo server, CallbackInfo ci) {
		if (!enabled()) return;

		// Used for `searchable$matches`
		this.serverName = this.server.name;

		// Used for `searchable$matches`
		if (SearchableConfig.INSTANCE.select_server_screen.match_motds.value()) {
			this.serverLabel = this.server.label;
		}
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
		if (!enabled() || serverName == null) {
			return original.call(graphics, textRenderer, serverName, x, y, color, shadowed);
		}

		if (!serverName.equals(this.serverName)) {
			this.serverName = serverName;
		}

		return graphics.drawText(textRenderer, (Text) this.serverNameMatchManager.getHighlightedText(serverName, this.query), x, y, color, shadowed);
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
		if (!enabled() || !SearchableConfig.INSTANCE.select_server_screen.match_motds.value() || label == null)
			return label;

		if (label instanceof Text serverLabel && !serverLabel.equals(this.serverLabel)) {
			this.serverLabel = serverLabel;
		}

		return this.serverLabelMatchManager.getHighlightedText(label, this.query);
	}

	@Unique
	private static boolean enabled() {
		return SearchableConfig.INSTANCE.select_server_screen.add_search.value() && SearchableConfig.INSTANCE.highlight_matches.value();
	}
}
