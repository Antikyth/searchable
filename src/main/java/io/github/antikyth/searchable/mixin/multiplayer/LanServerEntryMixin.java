package io.github.antikyth.searchable.mixin.multiplayer;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.antikyth.searchable.Util;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
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
public class LanServerEntryMixin implements SetQueryAccessor {
	@Final
	@Shadow
	protected LanServerInfo server;
	@Final
	@Shadow
	private static Text TITLE;

	@Unique
	private Text title;
	@Unique
	private Text titleWithHighlight;

	@Unique
	private String motd;
	@Unique
	private Text motdText;
	@Unique
	private Text motdWithHighlight;

	@Unique
	private String query = "";

	@Override
	public void searchable$setQuery(String query) {
		if (query != null && !query.equals(this.query)) {
			// Safe casts: input is Text, so output will be Text.
			this.titleWithHighlight = (Text) Util.textWithHighlight(query, this.title);
			this.motdWithHighlight = (Text) Util.textWithHighlight(query, this.motdText);

			this.query = query;
		}
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	protected void onConstructor(MultiplayerScreen screen, LanServerInfo server, CallbackInfo ci) {
		// Used in case a mixin uses a different title, so we can support that.
		this.title = TITLE;
		this.titleWithHighlight = this.title;

		// Used to check for changes to the MOTD.
		this.motd = this.server.getMotd();
		// Used to update the highlight.
		this.motdText = Text.literal(this.motd);
		this.motdWithHighlight = this.motdText;
	}

	@ModifyArg(method = "render", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I",
			ordinal = 0
	), index = 1)
	private Text drawTitleWithHighlight(Text title) {
		if (title == null) return null;

		// If the title has been changed (by another mixin), update the highlight first.
		if (!title.equals(this.title)) {
			this.title = title;
			this.titleWithHighlight = (Text) Util.textWithHighlight(this.query, this.title);
		}

		return this.titleWithHighlight;
	}

	@WrapOperation(method = "render", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/GuiGraphics.drawText (Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
			ordinal = 0
	))
	private int drawMotdWithHighlight(GuiGraphics graphics, TextRenderer textRenderer, String motd, int x, int y, int color, boolean shadowed, Operation<Integer> original) {
		if (motd == null) return original.call(graphics, textRenderer, null, x, y, color, shadowed);

		// If the MOTD has been changed, update the highlight first.
		if (!motd.equals(this.motd)) {
			this.motd = motd;
			this.motdText = Text.literal(this.motd);

			this.motdWithHighlight = (Text) Util.textWithHighlight(this.query, this.motdText);
		}

		return graphics.drawText(textRenderer, this.motdWithHighlight, x, y, color, shadowed);
	}
}