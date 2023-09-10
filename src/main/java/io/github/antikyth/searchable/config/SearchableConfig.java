package io.github.antikyth.searchable.config;

import io.github.antikyth.searchable.Searchable;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.loader.api.config.v2.QuiltConfig;

public class SearchableConfig extends ReflectiveConfig {
	public static final SearchableConfig INSTANCE = QuiltConfig.create(Searchable.MOD_ID, Searchable.MOD_ID, SearchableConfig.class);

	@Comment("""
		Whether the latest selected option should remain selected after being hidden and later unhidden due to search
		query changes.
		""")
	public final TrackedValue<Boolean> reselect_last_selection = value(true);

	@Comment("Whether the text that matches a search query should be highlighted.")
	public final TrackedValue<Boolean> highlight_matches = value(true);

	@Comment("Whether text should be searched using the query as a regular expression.")
	public final TrackedValue<Boolean> use_regex_matching = value(false);

	public final KeyBindScreenOptions keybinds_screen = new KeyBindScreenOptions();
	public final LanguageScreenOptions language_screen = new LanguageScreenOptions();
	public final ServerSelectScreenOptions select_server_screen = new ServerSelectScreenOptions();
	public final WorldSelectScreenOptions select_world_screen = new WorldSelectScreenOptions();
	public final EditGameRulesScreenOptions edit_gamerules_screen = new EditGameRulesScreenOptions();

	public static class KeyBindScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);

		@Comment("Whether key bind category names should be scanned for matching text during a search.")
		public final TrackedValue<Boolean> match_categories = value(true);
		@Comment("Whether the keys bound to their respective key binds should be scanned for matching text during a search.")
		public final TrackedValue<Boolean> match_bound_key = value(true);
	}

	public static class LanguageScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);
	}

	public static class ServerSelectScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);
		@Comment("""
			Whether the server selection screen's title should be changed to be consistent with the name of the
			singleplayer world selection screen.
			""")
		public final TrackedValue<Boolean> change_title = value(false);

		@Comment("Whether server descriptions should be scanned for matching text during a search.")
		public final TrackedValue<Boolean> match_motd = value(true);
	}

	public static class WorldSelectScreenOptions extends Section {
		@Comment("Whether world details should be scanned for matching text during a search.")
		public final TrackedValue<Boolean> match_world_details = value(false);
	}

	public static class EditGameRulesScreenOptions extends Section {
		public final TrackedValue<Boolean> add_search = value(true);

		@Comment("Whether game rule technical names should be shown under their titles.")
		public final TrackedValue<Boolean> show_technical_names = value(false);

		@Comment("Whether game rule category names should be scanned for matching text during a search.")
		public final TrackedValue<Boolean> match_categories = value(true);
		@Comment("Whether game rule descriptions should be scanned for matching text during a search.")
		public final TrackedValue<Boolean> match_descriptions = value(false);
		@Comment("Whether game rule technical names should be shown under their titles.")
		public final TrackedValue<Boolean> match_technical_names = value(true);
	}
}
