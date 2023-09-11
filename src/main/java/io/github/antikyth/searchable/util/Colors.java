/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.util;

import net.minecraft.util.Formatting;

public class Colors {
	public static final int BLACK;
	public static final int DARK_BLUE;
	public static final int DARK_GREEN;
	public static final int DARK_AQUA;
	public static final int DARK_RED;
	public static final int DARK_PURPLE;
	public static final int GOLD;
	public static final int GRAY;
	public static final int DARK_GRAY;
	public static final int BLUE;
	public static final int GREEN;
	public static final int AQUA;
	public static final int RED;
	public static final int LIGHT_PURPLE;
	public static final int YELLOW;
	public static final int WHITE;

	static {
		Integer black = Formatting.BLACK.getColorValue();
		BLACK = black == null ? 0x000000 : black;

		Integer darkBlue = Formatting.DARK_BLUE.getColorValue();
		DARK_BLUE = darkBlue == null ? 0x0000aa : darkBlue;

		Integer darkGreen = Formatting.DARK_GREEN.getColorValue();
		DARK_GREEN = darkGreen == null ? 0x00aa00 : darkGreen;

		Integer darkAqua = Formatting.DARK_AQUA.getColorValue();
		DARK_AQUA = darkAqua == null ? 0x00aaaa : darkAqua;

		Integer darkRed = Formatting.DARK_RED.getColorValue();
		DARK_RED = darkRed == null ? 0xaa0000 : darkRed;

		Integer darkPurple = Formatting.DARK_PURPLE.getColorValue();
		DARK_PURPLE = darkPurple == null ? 0xaa00aa : darkPurple;

		Integer gold = Formatting.GOLD.getColorValue();
		GOLD = gold == null ? 0xffaa00 : gold;

		Integer gray = Formatting.GRAY.getColorValue();
		GRAY = gray == null ? 0xaaaaaa : gray;

		Integer darkGray = Formatting.DARK_GRAY.getColorValue();
		DARK_GRAY = darkGray == null ? 0x555555 : darkGray;

		Integer blue = Formatting.BLUE.getColorValue();
		BLUE = blue == null ? 0x5555ff : blue;

		Integer green = Formatting.GREEN.getColorValue();
		GREEN = green == null ? 0x55ff55 : green;

		Integer aqua = Formatting.AQUA.getColorValue();
		AQUA = aqua == null ? 0x55ffff : aqua;

		Integer red = Formatting.RED.getColorValue();
		RED = red == null ? 0xff5555 : red;

		Integer lightPurple = Formatting.LIGHT_PURPLE.getColorValue();
		LIGHT_PURPLE = lightPurple == null ? 0xff55ff : lightPurple;

		Integer yellow = Formatting.YELLOW.getColorValue();
		YELLOW = yellow == null ? 0xffff55 : yellow;

		Integer white = Formatting.WHITE.getColorValue();
		WHITE = white == null ? 0xffffff : white;
	}
}
