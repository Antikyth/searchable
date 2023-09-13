/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.gui.widget;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.gui.screen.SearchableConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SearchableConfigButton extends TexturedButtonWidget {
	private static final Identifier ICONS_TEXTURE = new Identifier(Searchable.MOD_ID, "textures/gui/icons.png");
	public static final int CONFIG_BUTTON_SIZE = 20;

	public SearchableConfigButton(int x, int y, Screen parent) {
		this(x, y, button -> parent.getClient().setScreen(new SearchableConfigScreen(parent)));
	}

	public SearchableConfigButton(int x, int y, PressAction action) {
		super(x, y, CONFIG_BUTTON_SIZE, CONFIG_BUTTON_SIZE, 0, 0, ICONS_TEXTURE, action);

		this.setTooltip(Tooltip.create(Text.translatable(String.format("button.%s.openConfig.tooltip", Searchable.MOD_ID))));
	}
}
