/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.mixin;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntryListWidget.class)
public abstract class EntryListWidgetMixin<E extends EntryListWidget.Entry<E>> extends AbstractParentElement {
	@Shadow
	@Override
	public abstract List<? extends Element> children();

	@Shadow
	@Nullable
	public abstract E getSelectedOrNull();

	@Shadow
	public abstract void setSelected(@Nullable E entry);

	@Shadow
	public abstract void clearEntries();

	@Shadow
	public abstract int addEntry(E entry);

	@Shadow
	protected abstract void centerScrollOn(E entry);

	@Shadow
	public abstract void setScrollAmount(double amount);

	@Inject(method = "setSelected", at = @At("HEAD"))
	protected void onSetSelected(@Nullable E entry, CallbackInfo ci) {
	}
}
