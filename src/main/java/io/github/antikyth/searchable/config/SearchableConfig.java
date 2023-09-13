/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.config;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.datagen.lang.EnglishUs;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.loader.api.config.v2.QuiltConfig;

public class SearchableConfig extends ReflectiveConfig {
	public static final SearchableConfig INSTANCE = QuiltConfig.create(Searchable.MOD_ID, Searchable.MOD_ID, SearchableConfig.class);

	@Comment(EnglishUs.Config.SHOW_CONFIG_BUTTON_DESCRIPTION)
	public final TrackedValue<Boolean> show_config_button = value(true);
	@Comment(EnglishUs.Config.RESELECT_LAST_SELECTION_DESCRIPTION)
	public final TrackedValue<Boolean> reselect_last_selection = value(true);
	@Comment(EnglishUs.Config.HIGHLIGHT_MATCHES_DESCRIPTION)
	public final TrackedValue<Boolean> highlight_matches = value(true);
	public final TrackedValue<Boolean> use_regex_matching = value(false);

	public final SearchableConfigScreenOptions searchable_config_screen = new SearchableConfigScreenOptions();

	public final KeyBindScreenOptions keybinds_screen = new KeyBindScreenOptions();
	public final LanguageScreenOptions language_screen = new LanguageScreenOptions();
	@Comment(EnglishUs.Config.SelectServerScreen.DESCRIPTION)
	public final SelectServerScreenOptions select_server_screen = new SelectServerScreenOptions();
	@Comment(EnglishUs.Config.SelectWorldScreen.DESCRIPTION)
	public final SelectWorldScreenOptions select_world_screen = new SelectWorldScreenOptions();
	@Comment(EnglishUs.Config.EditGameRulesScreen.DESCRIPTION)
	public final EditGameRulesScreenOptions edit_gamerules_screen = new EditGameRulesScreenOptions();

	public static class SearchableConfigScreenOptions extends Section {
		@Comment(EnglishUs.Config.SearchableConfigScreen.SHOW_TECHNICAL_NAMES_DESCRIPTION)
		public final TrackedValue<Boolean> show_technical_names = value(false);

		@Comment(EnglishUs.Config.SearchableConfigScreen.MATCH_CATEGORIES_DESCRIPTION)
		public final TrackedValue<Boolean> match_categories = value(true);
		@Comment(EnglishUs.Config.SearchableConfigScreen.MATCH_DESCRIPTIONS_DESCRIPTION)
		public final TrackedValue<Boolean> match_descriptions = value(false);
		@Comment(EnglishUs.Config.SearchableConfigScreen.MATCH_TECHNICAL_NAMES_DESCRIPTION)
		public final TrackedValue<Boolean> match_technical_names = value(false);
	}

	public static class KeyBindScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);

		@Comment(EnglishUs.Config.KeyBindsScreen.MATCH_CATEGORIES_DESCRIPTION)
		public final TrackedValue<Boolean> match_categories = value(true);
		@Comment(EnglishUs.Config.KeyBindsScreen.MATCH_BOUND_KEYS_DESCRIPTION)
		public final TrackedValue<Boolean> match_bound_keys = value(true);
	}

	public static class LanguageScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);
	}

	public static class SelectServerScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);

		@Comment(EnglishUs.Config.SelectServerScreen.MATCH_MOTDS_DESCRIPTION)
		public final TrackedValue<Boolean> match_motds = value(true);
	}

	public static class SelectWorldScreenOptions extends Section {
		@Comment(EnglishUs.Config.SelectWorldScreen.MATCH_WORLD_DETAILS_DESCRIPTION)
		public final TrackedValue<Boolean> match_world_details = value(false);
	}

	public static class EditGameRulesScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);

		@Comment(EnglishUs.Config.EditGameRulesScreen.SHOW_TECHNICAL_NAMES_DESCRIPTION)
		public final TrackedValue<Boolean> show_technical_names = value(false);

		@Comment(EnglishUs.Config.EditGameRulesScreen.MATCH_CATEGORIES_DESCRIPTION)
		public final TrackedValue<Boolean> match_categories = value(true);
		@Comment(EnglishUs.Config.EditGameRulesScreen.MATCH_DESCRIPTIONS_DESCRIPTION)
		public final TrackedValue<Boolean> match_descriptions = value(false);
		@Comment(EnglishUs.Config.EditGameRulesScreen.MATCH_TECHNICAL_NAMES_DESCRIPTION)
		public final TrackedValue<Boolean> match_technical_names = value(true);
	}
}
