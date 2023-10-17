/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin;

import io.github.antikyth.searchable.accessor.MultilineTextLinesAccessor;
import net.minecraft.client.font.MultilineText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(targets = "net.minecraft.client.font.MultilineText$C_mebvwdjl")
public abstract class MultilineTextLinesMixin implements MultilineTextLinesAccessor {
	@Override
	@Accessor("field_26530")
	public abstract List<MultilineText.Line> getLines();
}
