package io.github.antikyth.searchable.mixin.singleplayer.gamerule;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.util.Util;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditGameRulesScreen.RuleCategoryWidget.class)
public class RuleCategoryWidgetMixin extends AbstractRuleWidgetMixin {
	@Final
	@Shadow
	Text name;
	@Unique
	private Text nameText;
	@Unique
	private Text nameWithHighlight;

	@Override
	protected void updateHighlight(String query) {
		if (!enabled()) return;

		this.nameWithHighlight = (Text) Util.textWithHighlight(query, this.nameText);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConstructor(EditGameRulesScreen editGameRulesScreen, Text name, CallbackInfo ci) {
		if (!enabled()) return;

		// Used to update the highlight in case the name is overridden by another mixin in the render method.
		this.nameText = this.name;
		this.updateHighlight(this.query);
	}

	@ModifyArg(method = "render", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/GuiGraphics.drawCenteredShadowedText (Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V",
			ordinal = 0
	), index = 1)
	private Text drawNameWithHighlight(Text name) {
		if (!enabled()) return name;

		if (name != null && !name.equals(this.nameText)) {
			this.nameText = name;
			this.updateHighlight(this.query);
		}

		return this.nameWithHighlight;
	}

	@Override
	protected boolean enabled() {
		return super.enabled() && Searchable.config.highlightMatches && Searchable.config.editGamerule.matchCategory;
	}
}
