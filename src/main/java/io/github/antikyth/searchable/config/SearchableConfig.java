/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.config;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.config.metadata.Description;
import io.github.antikyth.searchable.config.metadata.FormatArg;
import net.minecraft.util.Formatting;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.loader.api.config.v2.QuiltConfig;

public class SearchableConfig extends ReflectiveConfig {
	public static final SearchableConfig INSTANCE = QuiltConfig.create(Searchable.MOD_ID, Searchable.MOD_ID, SearchableConfig.class);

	public final TrackedValue<Boolean> reselect_last_selection = value(true);
	public final TrackedValue<Boolean> highlight_matches = value(true);
	public final TrackedValue<Boolean> use_regex_matching = value(false);

	public final SearchableConfigScreenOptions searchable_config_screen = new SearchableConfigScreenOptions();

	public final KeyBindScreenOptions keybinds_screen = new KeyBindScreenOptions();
	public final LanguageScreenOptions language_screen = new LanguageScreenOptions();
	public final ServerSelectScreenOptions select_server_screen = new ServerSelectScreenOptions();
	public final WorldSelectScreenOptions select_world_screen = new WorldSelectScreenOptions();
	public final EditGameRulesScreenOptions edit_gamerules_screen = new EditGameRulesScreenOptions();

	public static class SearchableConfigScreenOptions extends Section {
		public final TrackedValue<Boolean> show_technical_names = value(false);

		public final TrackedValue<Boolean> match_categories = value(true);
		public final TrackedValue<Boolean> match_descriptions = value(true);
		public final TrackedValue<Boolean> match_technical_names = value(true);
	}

	public static class KeyBindScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);

		@Description
		public final TrackedValue<Boolean> match_categories = value(true);
		@Description
		public final TrackedValue<Boolean> match_bound_keys = value(true);
	}

	public static class LanguageScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);
	}

	public static class ServerSelectScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);
		@Description(args = {
			@FormatArg(value = "multiplayer.title", type = FormatArg.Type.TRANSLATION_KEY, formattings = Formatting.BLUE),
			@FormatArg(value = "selectServer.title", type = FormatArg.Type.TRANSLATION_KEY, formattings = Formatting.BLUE)
		})
		public final TrackedValue<Boolean> change_title = value(false);

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
