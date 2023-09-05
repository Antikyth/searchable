package io.github.antikyth.searchable.mixin.singleplayer.gamerule;

import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;

@Mixin(EditGameRulesScreen.NamedRuleWidget.class)
public abstract class NamedRuleWidgetMixin extends AbstractRuleWidgetMixin {
	@Unique
	private Text name;

	@Inject(method = "<init>", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/font/TextRenderer.wrapLines (Lnet/minecraft/text/StringVisitable;I)Ljava/util/List;",
			ordinal = 0
	))
	public void onConstructor(EditGameRulesScreen instance, @Nullable List<OrderedText> description, Text name, CallbackInfo ci) {
		if (!enabled()) return;

		this.name = name;
	}

	@Unique
	@Override
	public boolean searchable$matches(String query) {
		String text = Formatting.strip(this.name.getString());
		assert text != null;

		return text.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)) || super.searchable$matches(query);
	}
}
