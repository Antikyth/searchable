/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.datagen.lang;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.datagen.annotation.Translation;
import io.github.antikyth.searchable.datagen.annotation.processor.DataGenProcessor;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class EnglishUs extends FabricLanguageProvider {
	public EnglishUs(FabricDataOutput dataGenerator) {
		super(dataGenerator, "en_us");
	}

	@Translation("controls.keybinds.search.hint")
	@Translation("editGamerule.search.hint")
	@Translation("option.language.search.hint")
	@Translation("selectServer.search.hint")
	@Translation("selectWorld.search.hint")
	@Translation("config.%s.search.hint")
	public static final String SEARCH_HINT = "Search...";
	public static final String ADD_SEARCH = "Add Search Box";

	public static class ModMenu {
		@Translation("modmenu.summaryTranslation.%s")
		public static final String SUMMARY = """
			Improves the singleplayer select world screen search and adds it to other GUIs.""";
	}

	@Translation("button.%s.openConfig.tooltip")
	public static final String CONFIG_BUTTON_TOOLTIP = "Configure search...";

	@Translation("controls.keybinds.search")
	public static final String KEY_BINDS_SEARCH_NARRATION = "search for key binds";
	@Translation("option.language.search")
	public static final String LANGUAGE_SEARCH_NARRATION = "search for languages";
	@Translation("selectServer.search")
	public static final String SELECT_SERVER_SEARCH_NARRATION = "search for multiplayer servers";
	@Translation("editGamerule.search")
	public static final String EDIT_GAME_RULE_SEARCH_NARRATION = "search for game rules";

	public static class Config {
		@Translation("config.%s.title")
		public static final String TITLE = "Searchable Options";
		@Translation("config.%s.search.narration")
		public static final String SEARCH_NARRATION = "search for config options";

		@Translation("config.%s.default")
		public static final String DEFAULT = "Default: %s";
		@Translation("config.%s.reset")
		public static final String RESET = "Reset";
		@Translation("config.%s.reset.narration")
		public static final String RESET_NARRATION = "reset %s to %s";


		@Translation.ConfigOption.Name("show_config_button")
		public static final String SHOW_CONFIG_BUTTON = "Show Config Button";
		@Translation.ConfigOption.Description("show_config_button")
		public static final String SHOW_CONFIG_BUTTON_DESCRIPTION = """
			Whether a button to open Searchable's config screen should be added next to search boxes added or modified \
			by Searchable.""";

		@Translation.ConfigOption.Name("reselect_last_selection")
		public static final String RESELECT_LAST_SELECTION = "Keep Selection Selected";
		@Translation.ConfigOption.Description("reselect_last_selection")
		public static final String RESELECT_LAST_SELECTION_DESCRIPTION = """
			Whether the latest selected option should remain selected after being hidden and later unhidden due to \
			search query changes.""";

		@Translation.ConfigOption.Name("highlight_matches")
		public static final String HIGHLIGHT_MATCHES = "Highlight Matches";
		@Translation.ConfigOption.Description("highlight_matches")
		public static final String HIGHLIGHT_MATCHES_DESCRIPTION = """
			Whether the text that matches a search query should be highlighted.""";

		@Translation.ConfigOption.Name("use_regex_matching")
		public static final String USE_REGEX_MATCHING = "RegEx Matching";

		@Translation.ConfigCategory("searchable_config_screen")
		public static class SearchableConfigScreen {
			@Translation.ConfigCategory.Name
			public static final String NAME = "Searchable Options Screen";

			@Translation.ConfigOption.Name("show_technical_names")
			public static final String SHOW_TECHNICAL_NAMES = "Show Technical Names";
			@Translation.ConfigOption.Description("show_technical_names")
			public static final String SHOW_TECHNICAL_NAMES_DESCRIPTION = """
				Whether config option technical names should be shown under their titles.""";

			@Translation.ConfigOption.Name("match_categories")
			public static final String MATCH_CATEGORIES = "Match Categories";
			@Translation.ConfigOption.Description("match_categories")
			public static final String MATCH_CATEGORIES_DESCRIPTION = """
				Whether config category names should be searched for matching text during a search.""";

			@Translation.ConfigOption.Name("match_descriptions")
			public static final String MATCH_DESCRIPTIONS = "Match Descriptions";
			@Translation.ConfigOption.Description("match_descriptions")
			public static final String MATCH_DESCRIPTIONS_DESCRIPTION = """
				Whether config option or category descriptions should be searched for matching text during a search.""";

			@Translation.ConfigOption.Name("match_technical_names")
			public static final String MATCH_TECHNICAL_NAMES = "Match Technical Names";
			@Translation.ConfigOption.Description("match_technical_names")
			public static final String MATCH_TECHNICAL_NAMES_DESCRIPTION = """
				Whether config option technical names should be searched for matching text during a search.""";
		}

		@Translation.ConfigCategory("keybinds_screen")
		public static class KeyBindsScreen {
			@Translation.ConfigCategory.Name
			public static final String NAME = "Key Binds Screen";

			@Translation.ConfigOption.Name("add_search")
			public static final String ADD_SEARCH = EnglishUs.ADD_SEARCH;

			@Translation.ConfigOption.Name("match_categories")
			public static final String MATCH_CATEGORIES = "Match Categories";
			@Translation.ConfigOption.Description("match_categories")
			public static final String MATCH_CATEGORIES_DESCRIPTION = """
				Whether key bind category names should be searched for matching text during a search.""";

			@Translation.ConfigOption.Name("match_bound_keys")
			public static final String MATCH_BOUND_KEYS = "Match Bound Keys";
			@Translation.ConfigOption.Description("match_bound_keys")
			public static final String MATCH_BOUND_KEYS_DESCRIPTION = """
				Whether the keys bound to their respective key binds should be searched for matching text during a \
				search.""";
		}

		@Translation.ConfigCategory("language_screen")
		public static class LanguageScreen {
			@Translation.ConfigCategory.Name
			public static final String NAME = "Languages Screen";

			@Translation.ConfigOption.Name("add_search")
			public static final String ADD_SEARCH = EnglishUs.ADD_SEARCH;
		}

		@Translation.ConfigCategory("select_server_screen")
		public static class SelectServerScreen {
			@Translation.ConfigCategory.Name
			public static final String NAME = "Select Server Screen";
			@Translation.ConfigCategory.Description
			public static final String DESCRIPTION = "The multiplayer server selection screen.";

			@Translation.ConfigOption.Name("add_search")
			public static final String ADD_SEARCH = EnglishUs.ADD_SEARCH;

			@Translation.ConfigOption.Name("match_motds")
			public static final String MATCH_MOTDS = "Match MOTDs";
			@Translation.ConfigOption.Description("match_motds")
			public static final String MATCH_MOTDS_DESCRIPTION = """
				Whether server descriptions should be searched for matching text during a search.""";
		}

		@Translation.ConfigCategory("select_world_screen")
		public static final class SelectWorldScreen {
			@Translation.ConfigCategory.Name
			public static final String NAME = "Select World Screen";
			@Translation.ConfigCategory.Description
			public static final String DESCRIPTION = "The singleplayer world selection screen.";

			@Translation.ConfigOption.Name("match_world_details")
			public static final String MATCH_WORLD_DETAILS = "Match World Details";
			@Translation.ConfigOption.Description("match_world_details")
			public static final String MATCH_WORLD_DETAILS_DESCRIPTION = """
				Whether world details should be searched for matching text during a search.""";
		}

		@Translation.ConfigCategory("edit_gamerules_screen")
		public static final class EditGameRulesScreen {
			@Translation.ConfigCategory.Name
			public static final String NAME = "Edit Game Rules Screen";
			@Translation.ConfigCategory.Description
			public static final String DESCRIPTION = "The game rules editing screen, used during world creation.";

			@Translation.ConfigOption.Name("add_search")
			public static final String ADD_SEARCH = EnglishUs.ADD_SEARCH;

			@Translation.ConfigOption.Name("show_technical_names")
			public static final String SHOW_TECHNICAL_NAMES = "Show Technical Names";
			@Translation.ConfigOption.Description("show_technical_names")
			public static final String SHOW_TECHNICAL_NAMES_DESCRIPTION = """
				Whether game rule technical names should be shown under their titles.""";

			@Translation.ConfigOption.Name("match_categories")
			public static final String MATCH_CATEGORIES = "Match Categories";
			@Translation.ConfigOption.Description("match_categories")
			public static final String MATCH_CATEGORIES_DESCRIPTION = """
				Whether game rule category names should be searched for matching text during a search.""";

			@Translation.ConfigOption.Name("match_descriptions")
			public static final String MATCH_DESCRIPTIONS = "Match Descriptions";
			@Translation.ConfigOption.Description("match_descriptions")
			public static final String MATCH_DESCRIPTIONS_DESCRIPTION = """
				Whether game rule descriptions should be searched for matching text during a search.""";

			@Translation.ConfigOption.Name("match_technical_names")
			public static final String MATCH_TECHNICAL_NAMES = "Match Technical Names";
			@Translation.ConfigOption.Description("match_technical_names")
			public static final String MATCH_TECHNICAL_NAMES_DESCRIPTION = """
				Whether game rule technical names should be searched for matching text during a search.""";
		}
	}

	@Override
	public void generateTranslations(TranslationBuilder translationBuilder) {
		this.processor.addAll(translationBuilder);
	}

	private final DataGenProcessor<EnglishUs> processor = DataGenProcessor.create(this, EnglishUs.class, Searchable.MOD_ID);
}
