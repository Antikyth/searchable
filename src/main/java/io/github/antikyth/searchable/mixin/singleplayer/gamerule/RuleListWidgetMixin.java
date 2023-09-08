package io.github.antikyth.searchable.mixin.singleplayer.gamerule;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.GetSearchBoxAccessor;
import io.github.antikyth.searchable.accessor.MatchesAccessor;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;
import io.github.antikyth.searchable.accessor.singleplayer.gamerule.AbstractRuleWidgetAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(EditGameRulesScreen.RuleListWidget.class)
public abstract class RuleListWidgetMixin extends ElementListWidget<EditGameRulesScreen.AbstractRuleWidget> implements SetQueryAccessor {
	@Shadow
	protected abstract void method_27637(Map.Entry<GameRules.Category, Map<GameRules.Key<?>, EditGameRulesScreen.AbstractRuleWidget>> entry);

	@Unique
	private Map<GameRules.Category, Map<GameRules.Key<?>, EditGameRulesScreen.AbstractRuleWidget>> map;

	@Unique
	private String query = "";

	@Unique
	@Override
	public void searchable$setQuery(String query) {
		if (enabled() && query != null && !query.equals(this.query)) {
			this.query = query;

			this.update();
		}
	}

	@Unique
	private boolean currentCategoryMatches = false;

	// Mixin will ignore this - required because of extending `ElementListWidget`
	public RuleListWidgetMixin(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	@Inject(method = "<init>", at = @At(
		value = "INVOKE",
		target = "net/minecraft/world/GameRules.accept (Lnet/minecraft/world/GameRules$Visitor;)V",
		ordinal = 0,
		shift = At.Shift.AFTER
	), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public void onConstructor(EditGameRulesScreen editGameRulesScreen, GameRules gameRules, CallbackInfo ci, Map<GameRules.Category, Map<GameRules.Key<?>, EditGameRulesScreen.AbstractRuleWidget>> map) {
		if (!enabled()) return;

		this.map = map;
		this.query = ((GetSearchBoxAccessor) editGameRulesScreen).searchable$getSearchBox().getText();
	}

	@ModifyArg(method = "<init>", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/widget/ElementListWidget.<init> (Lnet/minecraft/client/MinecraftClient;IIIII)V"
	), index = 3)
	private static int adjustTopCoord(int y) {
		return enabled() ? y + 5 : y;
	}

	@Inject(method = "method_27637", at = @At("HEAD"), cancellable = true)
	private void onFilterCategories(Map.Entry<GameRules.Category, Map<GameRules.Key<?>, EditGameRulesScreen.AbstractRuleWidget>> entry, CallbackInfo ci) {
		// If the category and nothing in it match, cancel adding it.
		if (enabled() && !this.filterCategory(this.query, entry)) ci.cancel();
	}

	// Set the query on the category.
	@ModifyArg(method = "method_27637", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/screen/world/EditGameRulesScreen$RuleListWidget.addEntry (Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;)I",
		ordinal = 0
	), index = 0)
	private EntryListWidget.Entry<EditGameRulesScreen.AbstractRuleWidget> onAddCategoryWidget(EntryListWidget.Entry<EditGameRulesScreen.AbstractRuleWidget> entry) {
		if (entry instanceof EditGameRulesScreen.AbstractRuleWidget ruleWidget) {
			((SetQueryAccessor) ruleWidget).searchable$setQuery(this.query);
		}

		return entry;
	}

	// Set the query and technical name.
	@Inject(method = "method_27638", at = @At("HEAD"))
	private void onAddRuleWidget(Map.Entry<GameRules.Key<?>, EditGameRulesScreen.AbstractRuleWidget> entry, CallbackInfo ci) {
		GameRules.Key<?> key = entry.getKey();
		EditGameRulesScreen.AbstractRuleWidget widget = entry.getValue();

		((AbstractRuleWidgetAccessor) widget).searchable$setTechnicalName(key.getName());
		((SetQueryAccessor) widget).searchable$setQuery(this.query);
	}

	@WrapWithCondition(method = "method_27638", at = @At(
		value = "INVOKE",
		target = "net/minecraft/client/gui/screen/world/EditGameRulesScreen$RuleListWidget.addEntry (Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;)I",
		ordinal = 0
	))
	private boolean onFilterRules(EditGameRulesScreen.RuleListWidget instance, EntryListWidget.Entry<EditGameRulesScreen.AbstractRuleWidget> entry) {
		if (!enabled()) return true;

		return entry instanceof EditGameRulesScreen.NamedRuleWidget namedRule && this.filterRule(this.query, namedRule);
	}

	@Unique
	private boolean filterCategory(String query, Map.Entry<GameRules.Category, Map<GameRules.Key<?>, EditGameRulesScreen.AbstractRuleWidget>> entry) {
		this.currentCategoryMatches = Searchable.config.editGamerule.matchCategory && ((MatchesAccessor) (Object) entry.getKey()).searchable$matches(query);

		if (this.currentCategoryMatches) {
			return true;
		} else {
			for (EditGameRulesScreen.AbstractRuleWidget rule : entry.getValue().values()) {
				if (this.filterRule(query, rule)) {
					return true;
				}
			}

			return false;
		}
	}

	@Unique
	private boolean filterRule(String query, EditGameRulesScreen.AbstractRuleWidget rule) {
		if (this.currentCategoryMatches) return true;

		if (rule instanceof EditGameRulesScreen.NamedRuleWidget namedRule) {
			return ((AbstractRuleWidgetAccessor) namedRule).searchable$matches(query);
		}

		return false;
	}

	@Unique
	private void update() {
		this.clearEntries();
		this.map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(this::method_27637);

		this.setScrollAmount(0.0);
	}

	@Unique
	private static boolean enabled() {
		return Searchable.config.editGamerule.enable;
	}
}
