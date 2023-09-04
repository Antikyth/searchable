/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class SearchablePreLaunch implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch(ModContainer mod) {
		// Initialise Mixin Extras.
		MixinExtrasBootstrap.init();
	}
}
