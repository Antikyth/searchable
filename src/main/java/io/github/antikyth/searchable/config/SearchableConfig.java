/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.config;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.datagen.EnglishUsLangProvider;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.loader.api.config.v2.QuiltConfig;

public class SearchableConfig extends ReflectiveConfig {
	public static final SearchableConfig INSTANCE = QuiltConfig.create(Searchable.MOD_ID, Searchable.MOD_ID, SearchableConfig.class);

	@Comment(EnglishUsLangProvider.Config.SHOW_CONFIG_BUTTON_DESCRIPTION)
	public final TrackedValue<Boolean> show_config_button = value(true);
	@Comment(EnglishUsLangProvider.Config.RESELECT_LAST_SELECTION_DESCRIPTION)
	public final TrackedValue<Boolean> reselect_last_selection = value(true);
	@Comment(EnglishUsLangProvider.Config.HIGHLIGHT_MATCHES_DESCRIPTION)
	public final TrackedValue<Boolean> highlight_matches = value(true);
	public final TrackedValue<Boolean> use_regex_matching = value(false);

	public final SearchableConfigScreenOptions searchable_config_screen = new SearchableConfigScreenOptions();

	public final KeyBindScreenOptions keybinds_screen = new KeyBindScreenOptions();
	public final LanguageScreenOptions language_screen = new LanguageScreenOptions();
	public final ServerSelectScreenOptions select_server_screen = new ServerSelectScreenOptions();
	public final WorldSelectScreenOptions select_world_screen = new WorldSelectScreenOptions();
	public final EditGameRulesScreenOptions edit_gamerules_screen = new EditGameRulesScreenOptions();

	public static class SearchableConfigScreenOptions extends Section {
		//		@Comment(EnglishUsLangProvider.SearchableConfigScreen.SHOW_TECHNICAL_NAMES_DESCRIPTION)
		public final TrackedValue<Boolean> show_technical_names = value(false);

		public final TrackedValue<Boolean> match_categories = value(true);
		public final TrackedValue<Boolean> match_descriptions = value(false);
		public final TrackedValue<Boolean> match_technical_names = value(false);
	}

	public static class KeyBindScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);

		public final TrackedValue<Boolean> match_categories = value(true);
		public final TrackedValue<Boolean> match_bound_keys = value(true);
	}

	public static class LanguageScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);
	}

	public static class ServerSelectScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);

		public final TrackedValue<Boolean> match_motds = value(true);
	}

	public static class WorldSelectScreenOptions extends Section {
		public final TrackedValue<Boolean> match_world_details = value(false);
	}

	public static class EditGameRulesScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);

		public final TrackedValue<Boolean> show_technical_names = value(false);

		public final TrackedValue<Boolean> match_categories = value(true);
		public final TrackedValue<Boolean> match_descriptions = value(false);
		public final TrackedValue<Boolean> match_technical_names = value(true);
	}
}
