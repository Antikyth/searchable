/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin;

import net.minecraft.client.font.MultilineText;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultilineText.Line.class)
public interface MultilineText$LineAccessor {
	@Invoker("<init>")
	static MultilineText.Line create(OrderedText text, int width) {
		throw new AssertionError();
	}

	@Accessor("text")
	OrderedText getText();
}
