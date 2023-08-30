package io.github.antikyth.searchable;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class Bootstrap implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch(ModContainer mod) {
		// Initialise Mixin Extras.
		MixinExtrasBootstrap.init();
	}
}
