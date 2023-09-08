package io.github.antikyth.searchable.mixin.multiplayer;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.MatchesAccessor;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerServerListWidget.LanServerEntry.class)
public class LanServerEntryMixin implements SetQueryAccessor, MatchesAccessor {
	@Final
	@Shadow
	protected LanServerInfo server;
	@Final
	@Shadow
	private static Text TITLE;

	@Unique
	private Text title = TITLE;
	@Unique
	private String motd;

	@Unique
	private final MatchManager titleMatchManager = new MatchManager();
	@Unique
	private final MatchManager motdMatchManager = new MatchManager();

	@Unique
	private String query = "";

	@Override
	public void searchable$setQuery(String query) {
		if (enabled() && query != null && !query.equals(this.query)) {
			this.query = query;
		}
	}

	@Override
	public boolean searchable$matches(String query) {
		return this.titleMatchManager.hasMatches(this.title, query) || (matchMotd() && this.motdMatchManager.hasMatches(this.motd, query));
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConstructor(MultiplayerScreen screen, LanServerInfo server, CallbackInfo ci) {
		this.motd = this.server.getMotd();
	}

	@ModifyArg(method = "render", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I",
		ordinal = 0
	), index = 1)
	private Text drawTitleWithHighlight(Text title) {
		if (!enabled() || title == null) return title;

		if (!title.equals(this.title)) {
			this.title = title;
		}

		return (Text) this.titleMatchManager.getHighlightedText(title, this.query);
	}

	@WrapOperation(method = "render", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
		ordinal = 0
	))
	private int drawMotdWithHighlight(GuiGraphics graphics, TextRenderer textRenderer, String motd, int x, int y, int color, boolean shadowed, Operation<Integer> original) {
		if (!enabled() || !Searchable.config.selectServer.matchMotd || motd == null) {
			return original.call(graphics, textRenderer, motd, x, y, color, shadowed);
		}

		if (!motd.equals(this.motd)) {
			this.motd = motd;
		}

		return graphics.drawText(textRenderer, (Text) this.motdMatchManager.getHighlightedText(motd, this.query), x, y, color, shadowed);
	}

	@Unique
	private static boolean matchMotd() {
		return Searchable.config.selectServer.enable && Searchable.config.selectServer.matchMotd;
	}

	@Unique
	private static boolean enabled() {
		return Searchable.config.selectServer.enable && Searchable.config.highlightMatches;
	}
}
