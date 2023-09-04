package io.github.antikyth.searchable.config;

import io.github.antikyth.searchable.Searchable;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.KeyBindsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;

@Config(name = Searchable.MOD_ID)
public class SearchableConfig implements ConfigData {
	/**
	 * Whether the last selected option should be restored when it is revealed again after the search query changes, if
	 * no other option was chosen.
	 */
	@ConfigEntry.Gui.Tooltip
	public boolean reselectLastSelection = true;
	/**
	 * Whether the text matching the search query should be highlighted.
	 */
	@ConfigEntry.Gui.Tooltip
	public boolean highlightMatches = true;

	/**
	 * Options relating to modifying the {@link KeyBindsScreen}.
	 */
	@ConfigEntry.Gui.CollapsibleObject
	public KeyBindScreenOptions keybinds = new KeyBindScreenOptions();
	/**
	 * Options relating to modifying the {@link LanguageOptionsScreen}.
	 */
	@ConfigEntry.Gui.CollapsibleObject
	public LanguageScreenOptions language = new LanguageScreenOptions();
	/**
	 * Options relating to modifying the {@link SelectWorldScreen}.
	 */
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Gui.CollapsibleObject
	public WorldSelectScreenOptions selectWorld = new WorldSelectScreenOptions();
	/**
	 * Options relating to modifying the {@link MultiplayerScreen}.
	 */
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Gui.CollapsibleObject
	public ServerSelectScreenOptions selectServer = new ServerSelectScreenOptions();

	public static class KeyBindScreenOptions {
		public boolean enable = true;

		@ConfigEntry.Gui.Tooltip
		public boolean matchCategory = true;
		@ConfigEntry.Gui.Tooltip
		public boolean matchBoundKey = true;
	}

	public static class LanguageScreenOptions {
		public boolean enable = true;
	}

	public static class ServerSelectScreenOptions {
		public boolean enable = true;
		@ConfigEntry.Gui.Tooltip
		public boolean changeSelectServerTitle = false;

		@ConfigEntry.Gui.Tooltip
		public boolean matchMotd = true;
	}

	public static class WorldSelectScreenOptions {
		@ConfigEntry.Gui.Tooltip
		public boolean matchWorldDetails = false;
	}
}
