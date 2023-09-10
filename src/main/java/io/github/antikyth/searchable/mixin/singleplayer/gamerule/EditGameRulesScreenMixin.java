package io.github.antikyth.searchable.mixin.singleplayer.gamerule;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.GetSearchBoxAccessor;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.accessor.TextFieldWidgetValidityAccessor;
import io.github.antikyth.searchable.util.Util;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.regex.PatternSyntaxException;

@Mixin(EditGameRulesScreen.class)
public class EditGameRulesScreenMixin extends Screen implements GetSearchBoxAccessor {
	@Unique
	private static final Text SEARCH_BOX_NARRATION_MESSAGE = Text.translatable("editGamerule.search");
	@Unique
	private static final Text SEARCH_BOX_HINT = Util.hint(Text.translatable("editGamerule.search.hint"));

	@Shadow
	private EditGameRulesScreen.RuleListWidget ruleListWidget;

	@Unique
	private TextFieldWidget searchBox;

	@Override
	public TextFieldWidget searchable$getSearchBox() {
		return this.searchBox;
	}

	// Mixin will ignore this - required because of extending `Screen`
	protected EditGameRulesScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("HEAD"))
	protected void onInit(CallbackInfo ci) {
		if (disabled()) return;

		Searchable.LOGGER.debug("adding search box to edit gamerules screen");

		this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox, SEARCH_BOX_NARRATION_MESSAGE);
		this.searchBox.setHint(SEARCH_BOX_HINT);
		this.searchBox.setChangedListener(query -> {
			Optional<PatternSyntaxException> valid = MatchManager.matcher().validateQueryError(query);

			((TextFieldWidgetValidityAccessor) this.searchBox).searchable$setValidity(valid);

			((SetQueryAccessor) this.ruleListWidget).searchable$setQuery(query);
		});

		this.addSelectableChild(this.searchBox);
		// Set the search box to be the initial focus.  This is to be consistent with the behavior of the world select
		// screen's search box.
		this.setInitialFocus(this.searchBox);
	}

	@ModifyArg(method = "render", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredShadowedText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V",
		ordinal = 0
	), index = 3)
	private int adjustTitleTextYCoord(int y) {
		if (disabled()) return y;

		return y - 12;
	}

	@Inject(method = "render", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/screen/world/EditGameRulesScreen$RuleListWidget.render (Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
		shift = At.Shift.AFTER
	))
	public void onRender(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (disabled()) return;

		this.searchBox.drawWidget(graphics, mouseX, mouseY, delta);
	}

	@Unique
	private static boolean disabled() {
		return !Searchable.config.editGamerule.enable;
	}
}
