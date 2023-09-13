/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.datagen;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.config.SearchableConfig;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import org.quiltmc.config.api.values.TrackedValue;

public class EnglishUsLangProvider extends FabricLanguageProvider {
	public EnglishUsLangProvider(FabricDataOutput dataGenerator) {
		super(dataGenerator, "en_us");
	}

	public static final String SEARCH_HINT = "Search...";
	public static final String ADD_SEARCH = "Add Search Box";

	public static class ModMenu {
		public static final String SUMMARY = "Makes more GUIs searchable.";
	}

	public static final String CONFIG_BUTTON_TOOLTIP = "Configure search...";

	public static final String RESET_CONFIG_OPTION = "Reset";
	public static final String RESET_CONFIG_OPTION_NARRATION = "reset %s to %s";

	public static final String KEY_BINDS_SEARCH_NARRATION = "search for key binds";
	public static final String LANGUAGE_SEARCH_NARRATION = "search for languages";
	public static final String SELECT_SERVER_SEARCH_NARRATION = "search for multiplayer servers";
	public static final String EDIT_GAME_RULE_SEARCH_NARRATION = "search for game rules";

	public static class Config {
		public static final String TITLE = "Searchable Options";
		public static final String SEARCH_NARRATION = "search for config options";
		public static final String DEFAULT = "Default: %s";


		public static final String SHOW_CONFIG_BUTTON = "Show Config Button";
		public static final String SHOW_CONFIG_BUTTON_DESCRIPTION = """
			Whether a button to open Searchable's config screen should be added next to search boxes added or modified \
			by Searchable.""";

		public static final String RESELECT_LAST_SELECTION = "Keep Selection Selected";
		public static final String RESELECT_LAST_SELECTION_DESCRIPTION = """
			Whether the latest selected option should remain selected after being hidden and later unhidden due to \
			search query changes.""";

		public static final String HIGHLIGHT_MATCHES = "Highlight Matches";
		public static final String HIGHLIGHT_MATCHES_DESCRIPTION = """
			Whether the text that matches a search query should be highlighted.""";

		public static final String USE_REGEX_MATCHING = "RegEx Matching";

		public static class SearchableConfigScreen {
			public static final String NAME = "Searchable Options Screen";

			public static final String SHOW_TECHNICAL_NAMES = "Show Technical Names";
			public static final String SHOW_TECHNICAL_NAMES_DESCRIPTION = showTechnicalNames("config option");

			public static final String MATCH_CATEGORIES = "Match Categories";
			public static final String MATCH_CATEGORIES_DESCRIPTION = matchDescription("config category names");

			public static final String MATCH_DESCRIPTIONS = "Match Descriptions";
			public static final String MATCH_DESCRIPTIONS_DESCRIPTION = matchDescription("config option or category descriptions");

			public static final String MATCH_TECHNICAL_NAMES = "Match Technical Names";
			public static final String MATCH_TECHNICAL_NAMES_DESCRIPTION = matchDescription("config option technical names");
		}

		public static class KeyBindsScreen {
			public static final String NAME = "Key Binds Screen";

			public static final String ADD_SEARCH = EnglishUsLangProvider.ADD_SEARCH;

			public static final String MATCH_CATEGORIES = "Match Categories";
			public static final String MATCH_CATEGORIES_DESCRIPTION = matchDescription("key bind category names");

			public static final String MATCH_BOUND_KEYS = "Match Bound Keys";
			public static final String MATCH_BOUND_KEYS_DESCRIPTION = matchDescription("the keys bound to their respective key binds");
		}

		public static class LanguageScreen {
			public static final String NAME = "Languages Screen";

			public static final String ADD_SEARCH = EnglishUsLangProvider.ADD_SEARCH;
		}

		public static class SelectServerScreen {
			public static final String NAME = "Select Server Screen";
			public static final String DESCRIPTION = "The multiplayer server selection screen.";

			public static final String ADD_SEARCH = EnglishUsLangProvider.ADD_SEARCH;

			public static final String MATCH_MOTDS = "Match MOTDs";
			public static final String MATCH_MOTDS_DESCRIPTION = matchDescription("server descriptions");
		}

		public static final class SelectWorldScreen {
			public static final String NAME = "Select World Screen";
			public static final String DESCRIPTION = "The singleplayer world selection screen.";

			public static final String MATCH_WORLD_DETAILS = "Match World Details";
			public static final String MATCH_WORLD_DETAILS_DESCRIPTION = matchDescription("world details");
		}

		public static final class EditGameRulesScreen {
			public static final String NAME = "Edit Game Rules Screen";
			public static final String DESCRIPTION = "The game rules editing screen, used during world creation.";

			public static final String ADD_SEARCH = EnglishUsLangProvider.ADD_SEARCH;

			public static final String SHOW_TECHNICAL_NAMES = "Show Technical Names";
			public static final String SHOW_TECHNICAL_NAMES_DESCRIPTION = showTechnicalNames("game rule");

			public static final String MATCH_CATEGORIES = "Match Categories";
			public static final String MATCH_CATEGORIES_DESCRIPTION = matchDescription("game rule category names");

			public static final String MATCH_DESCRIPTIONS = "Match Descriptions";
			public static final String MATCH_DESCRIPTIONS_DESCRIPTION = matchDescription("game rule descriptions");

			public static final String MATCH_TECHNICAL_NAMES = "Match Technical Names";
			public static final String MATCH_TECHNICAL_NAMES_DESCRIPTION = matchDescription("game rule technical names");
		}
	}

	@Override
	public void generateTranslations(TranslationBuilder tb) {
		tb.add(modid("modmenu.summaryTranslation.%s"), ModMenu.SUMMARY);

		tb.add(modid("search.%s.config.tooltip"), CONFIG_BUTTON_TOOLTIP);

		tb.add(modid("config.%s.reset"), RESET_CONFIG_OPTION);
		tb.add(modid("config.%s.reset.narration"), RESET_CONFIG_OPTION_NARRATION);

		tb.add("controls.keybinds.search", KEY_BINDS_SEARCH_NARRATION);
		tb.add("controls.keybinds.search.hint", SEARCH_HINT);

		tb.add("option.language.search", LANGUAGE_SEARCH_NARRATION);
		tb.add("option.language.search.hint", SEARCH_HINT);

		tb.add("selectServer.search", SELECT_SERVER_SEARCH_NARRATION);
		tb.add("selectServer.search.hint", SEARCH_HINT);

		tb.add("selectWorld.search.hint", SEARCH_HINT);

		tb.add("editGamerule.search", EDIT_GAME_RULE_SEARCH_NARRATION);
		tb.add("editGamerule.search.hint", SEARCH_HINT);


		tb.add(modid("config.%s.title"), Config.TITLE);

		tb.add(modid("config.%s.search"), Config.SEARCH_NARRATION);
		tb.add(modid("config.%s.search.hint"), SEARCH_HINT);

		tb.add(modid("config.%s.default"), Config.DEFAULT);

		//
		// Config
		//

		configOption(tb, SearchableConfig.INSTANCE.show_config_button, Config.SHOW_CONFIG_BUTTON, Config.SHOW_CONFIG_BUTTON_DESCRIPTION);
		configOption(tb, SearchableConfig.INSTANCE.reselect_last_selection, Config.RESELECT_LAST_SELECTION, Config.RESELECT_LAST_SELECTION_DESCRIPTION);
		configOption(tb, SearchableConfig.INSTANCE.highlight_matches, Config.HIGHLIGHT_MATCHES, Config.HIGHLIGHT_MATCHES_DESCRIPTION);
		configOption(tb, SearchableConfig.INSTANCE.use_regex_matching, Config.USE_REGEX_MATCHING);

		// Searchable Options Screen
		configCategory(tb, "searchable_config_screen", Config.SearchableConfigScreen.NAME);

		configOption(tb, SearchableConfig.INSTANCE.searchable_config_screen.show_technical_names, Config.SearchableConfigScreen.SHOW_TECHNICAL_NAMES, Config.SearchableConfigScreen.SHOW_TECHNICAL_NAMES_DESCRIPTION);

		configOption(tb, SearchableConfig.INSTANCE.searchable_config_screen.match_categories, Config.SearchableConfigScreen.MATCH_CATEGORIES, Config.SearchableConfigScreen.MATCH_CATEGORIES_DESCRIPTION);
		configOption(tb, SearchableConfig.INSTANCE.searchable_config_screen.match_descriptions, Config.SearchableConfigScreen.MATCH_DESCRIPTIONS, Config.SearchableConfigScreen.MATCH_DESCRIPTIONS_DESCRIPTION);
		configOption(tb, SearchableConfig.INSTANCE.searchable_config_screen.match_technical_names, Config.SearchableConfigScreen.MATCH_TECHNICAL_NAMES, Config.SearchableConfigScreen.MATCH_TECHNICAL_NAMES_DESCRIPTION);

		// Key Binds Screen
		configCategory(tb, "keybinds_screen", Config.KeyBindsScreen.NAME);

		configOption(tb, SearchableConfig.INSTANCE.keybinds_screen.add_search, Config.KeyBindsScreen.ADD_SEARCH);

		configOption(tb, SearchableConfig.INSTANCE.keybinds_screen.match_categories, Config.KeyBindsScreen.MATCH_CATEGORIES, Config.KeyBindsScreen.MATCH_CATEGORIES_DESCRIPTION);
		configOption(tb, SearchableConfig.INSTANCE.keybinds_screen.match_bound_keys, Config.KeyBindsScreen.MATCH_BOUND_KEYS, Config.KeyBindsScreen.MATCH_BOUND_KEYS_DESCRIPTION);

		// Languages Screen
		configCategory(tb, "language_screen", Config.LanguageScreen.NAME);

		configOption(tb, SearchableConfig.INSTANCE.language_screen.add_search, Config.LanguageScreen.ADD_SEARCH);

		// Select Server Screen
		configCategory(tb, "select_server_screen", Config.SelectServerScreen.NAME, Config.SelectServerScreen.DESCRIPTION);

		configOption(tb, SearchableConfig.INSTANCE.select_server_screen.add_search, Config.SelectServerScreen.ADD_SEARCH);

		configOption(tb, SearchableConfig.INSTANCE.select_server_screen.match_motds, Config.SelectServerScreen.MATCH_MOTDS, Config.SelectServerScreen.MATCH_MOTDS_DESCRIPTION);

		// Select World Screen
		configCategory(tb, "select_world_screen", Config.SelectWorldScreen.NAME, Config.SelectWorldScreen.DESCRIPTION);

		configOption(tb, SearchableConfig.INSTANCE.select_world_screen.match_world_details, Config.SelectWorldScreen.MATCH_WORLD_DETAILS, Config.SelectWorldScreen.MATCH_WORLD_DETAILS_DESCRIPTION);

		// Edit Game Rules Screen
		configCategory(tb, "edit_gamerules_screen", Config.EditGameRulesScreen.NAME, Config.EditGameRulesScreen.DESCRIPTION);

		configOption(tb, SearchableConfig.INSTANCE.edit_gamerules_screen.add_search, Config.EditGameRulesScreen.ADD_SEARCH);
		configOption(tb, SearchableConfig.INSTANCE.edit_gamerules_screen.show_technical_names, Config.EditGameRulesScreen.SHOW_TECHNICAL_NAMES, Config.EditGameRulesScreen.SHOW_TECHNICAL_NAMES_DESCRIPTION);

		configOption(tb, SearchableConfig.INSTANCE.edit_gamerules_screen.match_categories, Config.EditGameRulesScreen.MATCH_CATEGORIES, Config.EditGameRulesScreen.MATCH_CATEGORIES_DESCRIPTION);
		configOption(tb, SearchableConfig.INSTANCE.edit_gamerules_screen.match_descriptions, Config.EditGameRulesScreen.MATCH_DESCRIPTIONS, Config.EditGameRulesScreen.MATCH_DESCRIPTIONS_DESCRIPTION);
		configOption(tb, SearchableConfig.INSTANCE.edit_gamerules_screen.match_technical_names, Config.EditGameRulesScreen.MATCH_TECHNICAL_NAMES, Config.EditGameRulesScreen.MATCH_TECHNICAL_NAMES_DESCRIPTION);
	}

	private static String modid(String string) {
		return String.format(string, Searchable.MOD_ID);
	}

	private static String showTechnicalNames(String thing) {
		return String.format("Whether %s technical names should be shown under their titles.", thing);
	}

	private static String matchDescription(String things) {
		return String.format("Whether %s should be searched for matching text during a search.", things);
	}

	private static void configOption(TranslationBuilder builder, TrackedValue<?> configOption, String translation) {
		builder.add(String.format("config.%s.option.%s", Searchable.MOD_ID, configOption.key()), translation);
	}

	private static void configOption(TranslationBuilder builder, TrackedValue<?> configOption, String translation, String descriptionTranslation) {
		configOption(builder, configOption, translation);
		builder.add(String.format("config.%s.option.%s.description", Searchable.MOD_ID, configOption.key()), descriptionTranslation);
	}

	private static void configCategory(TranslationBuilder builder, String key, String translation) {
		builder.add(String.format("config.%s.category.%s", Searchable.MOD_ID, key), translation);
	}

	private static void configCategory(TranslationBuilder builder, String key, String translation, String descriptionTranslation) {
		configCategory(builder, key, translation);
		builder.add(String.format("config.%s.category.%s.description", Searchable.MOD_ID, key), descriptionTranslation);
	}
}
