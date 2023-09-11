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

	public static final String KEY_BINDS_SEARCH_NARRATION = "search for key binds";
	public static final String LANGUAGE_SEARCH_NARRATION = "search for languages";
	public static final String SELECT_SERVER_SEARCH_NARRATION = "search for multiplayer servers";
	public static final String EDIT_GAME_RULE_SEARCH_NARRATION = "search for game rules";

	public static class Config {
		public static final String TITLE = "Searchable Options";
		public static final String SEARCH_NARRATION = "search for config options";
		public static final String DEFAULT = "Default: %s";


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
			public static final String NAME_DESCRIPTION = "The multiplayer server selection screen.";

			public static final String ADD_SEARCH = EnglishUsLangProvider.ADD_SEARCH;

			public static final String CHANGE_TITLE = "Change Title";
			public static final String CHANGE_TITLE_DESCRIPTION = """
				Whether the server selection screen's title should be changed from §9"%s§9"§r to §9"%s§9"§r for \
				consistency with the singleplayer world selection screen.""";

			public static final String MATCH_MOTDS = "Match MOTDs";
			public static final String MATCH_MOTDS_DESCRIPTION = matchDescription("server descriptions");
		}

		public static final class SelectWorldScreen {
			public static final String NAME = "Select World Screen";
			public static final String NAME_DESCRIPTION = "The singleplayer world selection screen.";

			public static final String MATCH_WORLD_DETAILS = "Match World Details";
			public static final String MATCH_WORLD_DETAILS_DESCRIPTION = matchDescription("world details");
		}

		public static final class EditGameRulesScreen {
			public static final String NAME = "Edit Game Rules Screen";
			public static final String NAME_DESCRIPTION = "The game rules editing screen, used during world creation.";

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
	public void generateTranslations(TranslationBuilder translationBuilder) {
		translationBuilder.add(modid("modmenu.summaryTranslation.%s"), ModMenu.SUMMARY);

		translationBuilder.add("controls.keybinds.search", KEY_BINDS_SEARCH_NARRATION);
		translationBuilder.add("controls.keybinds.search.hint", SEARCH_HINT);

		translationBuilder.add("option.language.search", LANGUAGE_SEARCH_NARRATION);
		translationBuilder.add("option.language.search.hint", SEARCH_HINT);

		translationBuilder.add("selectServer.search", SELECT_SERVER_SEARCH_NARRATION);
		translationBuilder.add("selectServer.search.hint", SEARCH_HINT);

		translationBuilder.add("selectWorld.search.hint", SEARCH_HINT);

		translationBuilder.add("editGamerule.search", EDIT_GAME_RULE_SEARCH_NARRATION);
		translationBuilder.add("editGamerule.search.hint", SEARCH_HINT);


		translationBuilder.add(modid("config.%s.title"), Config.TITLE);

		translationBuilder.add(modid("config.%s.search"), Config.SEARCH_NARRATION);
		translationBuilder.add(modid("config.%s.search.hint"), SEARCH_HINT);

		translationBuilder.add(modid("config.%s.default"), Config.DEFAULT);

		//
		// Config
		//

		configOption(translationBuilder, SearchableConfig.INSTANCE.reselect_last_selection, Config.RESELECT_LAST_SELECTION);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.reselect_last_selection, Config.RESELECT_LAST_SELECTION_DESCRIPTION);

		configOption(translationBuilder, SearchableConfig.INSTANCE.highlight_matches, Config.HIGHLIGHT_MATCHES);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.highlight_matches, Config.HIGHLIGHT_MATCHES_DESCRIPTION);

		configOption(translationBuilder, SearchableConfig.INSTANCE.use_regex_matching, Config.USE_REGEX_MATCHING);


		// Searchable Options Screen
		configCategory(translationBuilder, "searchable_config_screen", Config.SearchableConfigScreen.NAME);

		configOption(translationBuilder, SearchableConfig.INSTANCE.searchable_config_screen.show_technical_names, Config.SearchableConfigScreen.SHOW_TECHNICAL_NAMES);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.searchable_config_screen.show_technical_names, Config.SearchableConfigScreen.SHOW_TECHNICAL_NAMES_DESCRIPTION);

		configOption(translationBuilder, SearchableConfig.INSTANCE.searchable_config_screen.match_categories, Config.SearchableConfigScreen.MATCH_CATEGORIES);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.searchable_config_screen.match_categories, Config.SearchableConfigScreen.MATCH_CATEGORIES_DESCRIPTION);

		configOption(translationBuilder, SearchableConfig.INSTANCE.searchable_config_screen.match_descriptions, Config.SearchableConfigScreen.MATCH_DESCRIPTIONS);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.searchable_config_screen.match_descriptions, Config.SearchableConfigScreen.MATCH_DESCRIPTIONS_DESCRIPTION);

		configOption(translationBuilder, SearchableConfig.INSTANCE.searchable_config_screen.match_technical_names, Config.SearchableConfigScreen.MATCH_TECHNICAL_NAMES);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.searchable_config_screen.match_technical_names, Config.SearchableConfigScreen.MATCH_TECHNICAL_NAMES_DESCRIPTION);


		// Key Binds Screen
		configCategory(translationBuilder, "keybinds_screen", Config.KeyBindsScreen.NAME);

		configOption(translationBuilder, SearchableConfig.INSTANCE.keybinds_screen.add_search, Config.KeyBindsScreen.ADD_SEARCH);

		configOption(translationBuilder, SearchableConfig.INSTANCE.keybinds_screen.match_categories, Config.KeyBindsScreen.MATCH_CATEGORIES);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.keybinds_screen.match_categories, Config.KeyBindsScreen.MATCH_CATEGORIES_DESCRIPTION);

		configOption(translationBuilder, SearchableConfig.INSTANCE.keybinds_screen.match_bound_keys, Config.KeyBindsScreen.MATCH_BOUND_KEYS);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.keybinds_screen.match_bound_keys, Config.KeyBindsScreen.MATCH_BOUND_KEYS_DESCRIPTION);


		// Languages Screen
		configCategory(translationBuilder, "language_screen", Config.LanguageScreen.NAME);

		configOption(translationBuilder, SearchableConfig.INSTANCE.language_screen.add_search, Config.LanguageScreen.ADD_SEARCH);


		// Select Server Screen
		configCategory(translationBuilder, "select_server_screen", Config.SelectServerScreen.NAME);
		configCategoryDesc(translationBuilder, "select_server_screen", Config.SelectServerScreen.NAME_DESCRIPTION);

		configOption(translationBuilder, SearchableConfig.INSTANCE.select_server_screen.add_search, Config.SelectServerScreen.ADD_SEARCH);

		configOption(translationBuilder, SearchableConfig.INSTANCE.select_server_screen.change_title, Config.SelectServerScreen.CHANGE_TITLE);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.select_server_screen.change_title, Config.SelectServerScreen.CHANGE_TITLE_DESCRIPTION);

		configOption(translationBuilder, SearchableConfig.INSTANCE.select_server_screen.match_motds, Config.SelectServerScreen.MATCH_MOTDS);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.select_server_screen.match_motds, Config.SelectServerScreen.MATCH_MOTDS_DESCRIPTION);


		// Select World Screen
		configCategory(translationBuilder, "select_world_screen", Config.SelectWorldScreen.NAME);
		configCategoryDesc(translationBuilder, "select_world_screen", Config.SelectWorldScreen.NAME_DESCRIPTION);

		configOption(translationBuilder, SearchableConfig.INSTANCE.select_world_screen.match_world_details, Config.SelectWorldScreen.MATCH_WORLD_DETAILS);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.select_world_screen.match_world_details, Config.SelectWorldScreen.MATCH_WORLD_DETAILS_DESCRIPTION);


		// Edit Game Rules Screen
		configCategory(translationBuilder, "edit_gamerules_screen", Config.EditGameRulesScreen.NAME);
		configCategoryDesc(translationBuilder, "edit_gamerules_screen", Config.EditGameRulesScreen.NAME_DESCRIPTION);

		configOption(translationBuilder, SearchableConfig.INSTANCE.edit_gamerules_screen.add_search, Config.EditGameRulesScreen.ADD_SEARCH);

		configOption(translationBuilder, SearchableConfig.INSTANCE.edit_gamerules_screen.show_technical_names, Config.EditGameRulesScreen.SHOW_TECHNICAL_NAMES);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.edit_gamerules_screen.show_technical_names, Config.EditGameRulesScreen.SHOW_TECHNICAL_NAMES_DESCRIPTION);

		configOption(translationBuilder, SearchableConfig.INSTANCE.edit_gamerules_screen.match_categories, Config.EditGameRulesScreen.MATCH_CATEGORIES);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.edit_gamerules_screen.match_categories, Config.EditGameRulesScreen.MATCH_CATEGORIES_DESCRIPTION);

		configOption(translationBuilder, SearchableConfig.INSTANCE.edit_gamerules_screen.match_descriptions, Config.EditGameRulesScreen.MATCH_DESCRIPTIONS);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.edit_gamerules_screen.match_descriptions, Config.EditGameRulesScreen.MATCH_DESCRIPTIONS_DESCRIPTION);

		configOption(translationBuilder, SearchableConfig.INSTANCE.edit_gamerules_screen.match_technical_names, Config.EditGameRulesScreen.MATCH_TECHNICAL_NAMES);
		configOptionDesc(translationBuilder, SearchableConfig.INSTANCE.edit_gamerules_screen.match_technical_names, Config.EditGameRulesScreen.MATCH_TECHNICAL_NAMES_DESCRIPTION);
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

	private static void configOptionDesc(TranslationBuilder builder, TrackedValue<?> configOption, String translation) {
		builder.add(String.format("config.%s.option.%s.description", Searchable.MOD_ID, configOption.key()), translation);
	}

	private static void configCategory(TranslationBuilder builder, String key, String translation) {
		builder.add(String.format("config.%s.category.%s", Searchable.MOD_ID, key), translation);
	}

	private static void configCategoryDesc(TranslationBuilder builder, String key, String translation) {
		builder.add(String.format("config.%s.category.%s.description", Searchable.MOD_ID, key), translation);
	}
}
