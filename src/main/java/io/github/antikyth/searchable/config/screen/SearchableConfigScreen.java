/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.config.screen;

import com.google.common.collect.ImmutableList;
import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.accessor.TextFieldWidgetValidityAccessor;
import io.github.antikyth.searchable.config.SearchableConfig;
import io.github.antikyth.searchable.config.metadata.Description;
import io.github.antikyth.searchable.util.Colors;
import io.github.antikyth.searchable.util.Pair;
import io.github.antikyth.searchable.util.Util;
import io.github.antikyth.searchable.util.function.MatcherTriFunctionTempCache;
import io.github.antikyth.searchable.util.match.MatchManager;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueKey;
import org.quiltmc.config.api.values.ValueTreeNode;

import java.util.*;
import java.util.regex.PatternSyntaxException;

public class SearchableConfigScreen extends Screen {
	protected final Screen parent;

	private static final Text SEARCH_BOX_NARRATION_MESSAGE = Text.translatable(String.format("config.%s.search", Searchable.MOD_ID));
	private static final Text SEARCH_BOX_HINT = Util.hint(Text.translatable(String.format("config.%s.search.hint", Searchable.MOD_ID)));

	private static final int DESCRIPTION_LINE_LIMIT = 150;

	private final GridWidget grid = new GridWidget().setColumnSpacing(10);

	private TextFieldWidget searchBox;
	private String currentQuery;

	private SearchableConfigEntryListWidget entryListWidget;
	private final Set<AbstractConfigOptionEntry<?>> invalidConfigOptionEntries = new HashSet<>();
	private ButtonWidget doneButton;

	public SearchableConfigScreen(Screen parent) {
		super(Text.translatable("config.searchable.title"));

		this.parent = parent;
	}

	@Override
	protected void init() {
		// Search box
		this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox, SEARCH_BOX_NARRATION_MESSAGE);
		this.searchBox.setHint(SEARCH_BOX_HINT);
		this.searchBox.setChangedListener(query -> {
			Optional<PatternSyntaxException> validityError = MatchManager.matcher().validateQueryError(query);

			((TextFieldWidgetValidityAccessor) this.searchBox).searchable$setValidity(validityError);

			if (!query.equals(this.currentQuery)) {
				this.currentQuery = query;

				this.entryListWidget.updateEntries();
			}
		});

		this.addSelectableChild(this.searchBox);
		this.setInitialFocus(this.searchBox);

		// Entry list widget
		this.entryListWidget = new SearchableConfigEntryListWidget(48, this.height - 32, 24, this.entryListWidget);
		this.addSelectableChild(this.entryListWidget);

		// Buttons
		GridWidget.AdditionHelper additionHelper = this.grid.createAdditionHelper(2);

		this.doneButton = additionHelper.add(ButtonWidget.builder(CommonTexts.DONE, button -> {
			SearchableConfig.INSTANCE.values().forEach(SearchableConfigScreen::applyConfigOptionOverride);

			this.getClient().setScreen(this.parent);
		}).build());
		additionHelper.add(ButtonWidget.builder(CommonTexts.CANCEL, button -> this.closeScreen()).build());

		this.repositionElements();
		this.grid.visitWidgets(this::addDrawableChild);
	}

	@Override
	protected void repositionElements() {
		this.grid.setPosition(this.width / 2 - 155, this.height - 28);
		this.grid.arrangeElements();
	}

	@Override
	public void closeScreen() {
		SearchableConfig.INSTANCE.values().forEach(TrackedValue::removeOverride);

		this.getClient().setScreen(this.parent);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		// Config options
		this.entryListWidget.render(graphics, mouseX, mouseY, delta);
		// Search box
		this.searchBox.render(graphics, mouseX, mouseY, delta);

		// Title
		graphics.drawCenteredShadowedText(this.textRenderer, this.title, this.width / 2, 8, Colors.WHITE);

		// Buttons 'n' stuff
		super.render(graphics, mouseX, mouseY, delta);
	}

	private void updateDoneButton() {
		this.doneButton.active = this.invalidConfigOptionEntries.isEmpty();
	}

	<T> void markInvalid(AbstractConfigOptionEntry<T> configOptionEntry) {
		this.invalidConfigOptionEntries.add(configOptionEntry);
		this.updateDoneButton();
	}

	<T> void markValid(AbstractConfigOptionEntry<T> configOptionEntry) {
		this.invalidConfigOptionEntries.remove(configOptionEntry);
		this.updateDoneButton();
	}

	public abstract class AbstractEntry extends ElementListWidget.Entry<AbstractEntry> {
		@Nullable
		protected Text description;
		@Nullable
		protected List<OrderedText> tooltip;

		protected final MatchManager descriptionMatchManager = new MatchManager();

		public AbstractEntry(@Nullable Text description) {
			this.description = description;
		}

		public boolean matches() {
			return this.description != null && matchDescription() && this.descriptionMatchManager.hasMatches(this.description, this.getQuery());
		}

		protected void updateTooltip(@Nullable Text description, String query) {
			if (description == null) {
				this.tooltip = null;
			} else {
				this.tooltip = this.createTooltip(description, query);
			}
		}

		protected List<OrderedText> createTooltip(@NotNull Text description, String query) {
			return this.createDescriptionTooltip(description, query);
		}

		protected ArrayList<OrderedText> createDescriptionTooltip(@NotNull Text description, String query) {
			return this.tooltipHighlightCache.apply(MatchManager.matcher(), description, query, (matcher, _description, _query) -> {
				StringVisitable descriptionText = _description;
				if (highlightMatches() && matchDescription()) {
					descriptionText = this.descriptionMatchManager.getHighlightedText(_description, _query);
				}

				return new ArrayList<>(SearchableConfigScreen.this.textRenderer.wrapLines(descriptionText, DESCRIPTION_LINE_LIMIT));
			});
		}

		@Override
		public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			if (hovered) updateTooltip(this.description, this.getQuery());
		}

		protected String getQuery() {
			return SearchableConfigScreen.this.searchBox.getText();
		}

		protected static boolean highlightMatches() {
			return SearchableConfig.INSTANCE.highlight_matches.value();
		}

		protected static boolean matchDescription() {
			return SearchableConfig.INSTANCE.searchable_config_screen.match_descriptions.value();
		}

		protected final MatcherTriFunctionTempCache<StringVisitable, String, ArrayList<OrderedText>> tooltipHighlightCache = MatcherTriFunctionTempCache.create();
	}

	/**
	 * An entry for a {@linkplain TrackedValue config option}.
	 */
	public abstract class AbstractConfigOptionEntry<T> extends AbstractEntry {
		protected final List<ClickableWidget> children = new ArrayList<>();

		protected TrackedValue<T> configOption;

		protected Text name;
		protected Text technicalName;

		protected final MatchManager nameMatchManager = new MatchManager();
		protected final MatchManager technicalNameMatchManager = new MatchManager();
		protected final MatchManager tooltipNameMatchManager = new MatchManager();

		public AbstractConfigOptionEntry(Text name, Text technicalName, @Nullable Text description, TrackedValue<T> configOption) {
			super(description);

			this.configOption = configOption;

			this.name = name;
			this.technicalName = technicalName;
		}

		@Override
		public boolean matches() {
			return nameMatchManager.hasMatches(this.name, this.getQuery())
				|| (matchTechnicalName() && this.technicalNameMatchManager.hasMatches(this.technicalName, this.getQuery()))
				|| super.matches();
		}

		protected void drawName(@NotNull GuiGraphics graphics, int x, int y) {
			graphics.drawText(SearchableConfigScreen.this.textRenderer, this.getRenderedName(), x, showTechnicalName() ? y : y + 5, Colors.WHITE, false);

			if (showTechnicalName()) {
				graphics.drawText(SearchableConfigScreen.this.textRenderer, this.getRenderedTechnicalName(), x, y + 10, Colors.WHITE, false);
			}
		}

		protected Text getRenderedName() {
			if (highlightMatches()) {
				return (Text) this.nameMatchManager.getHighlightedText(this.name, this.getQuery());
			}

			return this.name;
		}

		protected Text getRenderedTechnicalName() {
			if (highlightMatches() && matchTechnicalName()) {
				return (Text) this.technicalNameMatchManager.getHighlightedText(this.technicalName, this.getQuery());
			}

			return this.technicalName;
		}

		@Override
		protected List<OrderedText> createTooltip(@NotNull Text description, String query) {
			ArrayList<OrderedText> tooltip = new ArrayList<>();

			Text tooltipName = this.getRenderedTooltipName();
			if (tooltipName != null) tooltip.add(tooltipName.asOrderedText());

			tooltip.addAll(this.createDescriptionTooltip(description, query));

			Text defaultValue = Text.literal(this.configOption.getDefaultValue().toString());
			MutableText defaultText = Text.translatable(String.format("config.%s.default", Searchable.MOD_ID), defaultValue);

			tooltip.add(defaultText.formatted(Formatting.GRAY).asOrderedText());

			return tooltip;
		}

		protected Text getRenderedTooltipName() {
			if (this.technicalName == null) return null;

			Text name = this.technicalName.copyContentOnly().formatted(Formatting.YELLOW);

			if (highlightMatches() && matchTechnicalName()) {
				return (Text) this.tooltipNameMatchManager.getHighlightedText(name, this.getQuery());
			}

			return name;
		}

		@Override
		public List<? extends Element> children() {
			return this.children;
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return this.children;
		}

		protected static boolean showTechnicalName() {
			return SearchableConfig.INSTANCE.searchable_config_screen.show_technical_names.value();
		}

		protected static boolean matchTechnicalName() {
			return SearchableConfig.INSTANCE.searchable_config_screen.match_technical_names.value();
		}
	}

	/**
	 * An entry for a {@link Boolean} {@linkplain TrackedValue config option}.
	 */
	public class BooleanConfigOptionEntry extends AbstractConfigOptionEntry<Boolean> {
		private final CyclingButtonWidget<Boolean> toggleButton;

		public BooleanConfigOptionEntry(Text name, Text technicalName, @Nullable Text description, TrackedValue<Boolean> configOption) {
			super(name, technicalName, description, configOption);

			this.toggleButton = CyclingButtonWidget.onOffBuilder(configOption.value())
				.omitKeyText()
				.build(10, 0, 44, 20, name, (button, value) -> configOption.setOverride(value));

			this.children.add(this.toggleButton);
		}

		@Override
		public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

			this.drawName(graphics, x, y);

			this.toggleButton.setX(x + entryWidth - 45);
			this.toggleButton.setY(y);

			this.toggleButton.render(graphics, mouseX, mouseY, tickDelta);
		}
	}

	/**
	 * An entry for a {@linkplain org.quiltmc.config.api.ReflectiveConfig.Section section}.
	 */
	public class CategoryEntry extends AbstractEntry {
		private final Text name;
		private final MatchManager matchManager;

		public CategoryEntry(Text name, @Nullable Text description) {
			this(name, description, new MatchManager());
		}

		public CategoryEntry(Text name, @Nullable Text description, MatchManager matchManager) {
			super(description);

			this.name = name;
			this.matchManager = matchManager;
		}

		@Override
		public boolean matches() {
			return (matchName() && this.matchManager.hasMatches(this.name, this.getQuery())) || super.matches();
		}

		@Override
		public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

			// Draw category name.
			graphics.drawCenteredShadowedText(SearchableConfigScreen.this.textRenderer, this.getRenderedName(), x + entryWidth / 2, y + 5, Colors.WHITE);
		}

		private Text getRenderedName() {
			if (highlightMatches() && matchName()) {
				return (Text) this.matchManager.getHighlightedText(this.name, this.getQuery());
			}

			return this.name;
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return ImmutableList.of(new Selectable() {
				@Override
				public SelectionType getType() {
					return SelectionType.HOVERED;
				}

				@Override
				public void appendNarrations(NarrationMessageBuilder builder) {
					builder.put(NarrationPart.TITLE, CategoryEntry.this.name);
				}
			});
		}

		@Override
		public List<? extends Element> children() {
			return ImmutableList.of();
		}

		private static boolean matchName() {
			return SearchableConfig.INSTANCE.searchable_config_screen.match_categories.value();
		}
	}

	public class SearchableConfigEntryListWidget extends ElementListWidget<AbstractEntry> {
		private final Map<@Nullable ValueKey, List<AbstractConfigOptionEntry<?>>> configOptionMap;
		// I'm not sure why this is a warning? Indeed, there is no need to update this map - that is why it is not
		// updated.
		@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
		// Map<sectionKey, Map<trackedValueKey, configOptionEntry>>
		private final Map<ValueKey, CategoryEntry> categoryMap;

		public SearchableConfigEntryListWidget(int top, int bottom, int entryHeight, @Nullable SearchableConfigEntryListWidget prev) {
			super(SearchableConfigScreen.this.client, SearchableConfigScreen.this.width, SearchableConfigScreen.this.height, top, bottom, entryHeight);

			if (prev != null) {
				this.configOptionMap = prev.configOptionMap;
				this.categoryMap = prev.categoryMap;
			} else {
				this.configOptionMap = new LinkedHashMap<>();
				this.categoryMap = new HashMap<>();

				SearchableConfig.INSTANCE.nodes().forEach(node -> this.addNodeToMap(null, node));
			}

			updateEntries();
		}

		@Override
		public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
			super.render(graphics, mouseX, mouseY, delta);

			var hoveredEntry = this.getHoveredEntry();
			if (hoveredEntry != null && hoveredEntry.tooltip != null) {
				SearchableConfigScreen.this.setDeferredTooltip(hoveredEntry.tooltip);
			}
		}

		private void updateEntries() {
			this.clearEntries();

			configOptionMap.forEach((categoryKey, options) -> {
				if (categoryKey != null) {
					// These `options` have a category.

					CategoryEntry category = this.categoryMap.get(categoryKey);

					if (category.matches()) {
						// category matches: add it and all its options.

						// Add the category entry.
						this.addEntry(category);

						// Add all the config option entries.
						options.forEach(this::addEntry);
					} else {
						// Whether the category has already been added.
						final boolean[] categoryAdded = {false};

						// Add any options that match the query.
						options.stream().filter(AbstractConfigOptionEntry::matches).forEach(entry -> {
							// If the category hasn't already been added, add it.
							if (!categoryAdded[0]) {
								this.addEntry(category);

								categoryAdded[0] = true;
							}

							this.addEntry(entry);
						});
					}
				} else {
					// These `options` do not have a category.

					// Add any options that match the query.
					options.stream().filter(AbstractConfigOptionEntry::matches).forEach(this::addEntry);
				}
			});

			this.setScrollAmount(0.0);
		}

		// unchecked cast warnings are suppressed because they _are_ checked by doing `instanceof` on their `value()`s.
		@SuppressWarnings("unchecked")
		private void addNodeToMap(@Nullable ValueTreeNode.Section parentSection, ValueTreeNode node) {
			if (node instanceof ValueTreeNode.Section sectionNode) {
				// If it is a section, add all its nodes.

				sectionNode.forEach(innerNode -> this.addNodeToMap(sectionNode, innerNode));
			} else if (node instanceof TrackedValue<?> configOption) {
				// If it is a config option, add an entry.

				if (configOption.value() instanceof Boolean) {
					ValueKey categoryKey;

					if (parentSection == null) {
						categoryKey = null;
					} else {
						categoryKey = parentSection.key();

						// Create the category entry if it hasn't been created before.
						this.categoryMap.computeIfAbsent(categoryKey, category -> {
							String translationKey = categoryTranslationKey(category);

							Text name = Text.translatable(translationKey).formatted(Formatting.YELLOW, Formatting.BOLD);
							Text description = descriptionText(parentSection, translationKey);

							return new CategoryEntry(name, description);
						});
					}

					this.addEntryToMap(categoryKey, (TrackedValue<Boolean>) configOption, BooleanConfigOptionEntry::new);
				}
			}
		}

		private <T> void addEntryToMap(@Nullable ValueKey categoryKey, TrackedValue<T> configOption, ConfigOptionEntryFactory<T> entryFactory) {
			ValueKey key = configOption.key();
			String translationKey = configOptionTranslationKey(key);

			Text name = Text.translatable(translationKey);
			Text technicalName = Util.technicalName(Text.literal(key.getLastComponent()));

			Text description = descriptionText(configOption, translationKey);

			configOptionMap.computeIfAbsent(categoryKey, category -> new ArrayList<>())
				.add(entryFactory.create(name, technicalName, description, configOption));
		}
	}

	private static <T> void applyConfigOptionOverride(TrackedValue<T> configOption) {
		configOption.setValue(configOption.value(), true);
		configOption.removeOverride();
	}

	@Nullable
	private static Text descriptionText(ValueTreeNode node, String nodeTranslationKey) {
		MutableText description;

		String translationKey = descriptionTranslationKey(nodeTranslationKey);

		if (node.hasMetadata(Description.TYPE)) {
			// If `@Description` is used.

			Pair<String, Text[]> pair = node.metadata(Description.TYPE);

			String descriptionTranslationKey = pair.first;
			descriptionTranslationKey = descriptionTranslationKey.isEmpty() ? translationKey : descriptionTranslationKey;

			description = Text.translatable(descriptionTranslationKey, (Object[]) pair.second);
		} else if (I18n.hasTranslation(translationKey)) {
			// `@Description` is not used, but there is a translation provided for the default description key.

			description = Text.translatable(translationKey);
		} else if (node.hasMetadata(Comment.TYPE)) {
			// If `@Description` is not used but `@Comment` is.

			description = Text.empty();

			Iterator<String> comments = node.metadata(Comment.TYPE).iterator();
			if (comments.hasNext()) {
				// First comment.
				description.append(comments.next());
				// All the rest, with a line in between.
				comments.forEachRemaining(additionalComment -> description.append("\n\n").append(additionalComment));
			}
		} else {
			description = null;
		}

		return description;
	}

	private static String descriptionTranslationKey(String translationKey) {
		return String.format("%s.description", translationKey);
	}

	private static String categoryTranslationKey(ValueKey categoryKey) {
		return String.format("config.%s.category.%s", Searchable.MOD_ID, categoryKey.toString());
	}

	private static String configOptionTranslationKey(ValueKey configOptionKey) {
		return String.format("config.%s.option.%s", Searchable.MOD_ID, configOptionKey.toString());
	}

	@FunctionalInterface
	public interface ConfigOptionEntryFactory<T> {
		SearchableConfigScreen.AbstractConfigOptionEntry<T> create(Text name, Text technicalName, @Nullable Text description, TrackedValue<T> configOption);
	}
}
