/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import io.github.antikyth.searchable.config.metadata.Description;
import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Searchable implements PreLaunchEntrypoint, ClientModInitializer {
	public static final String NAME = "Searchable";
	public static final String MOD_ID = "searchable";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	public static final int SEARCH_BOX_WIDTH = 200;
	public static final int CONFIG_BUTTON_OFFSET = 3;

	@Override
	public void onPreLaunch(ModContainer mod) {
		// Initialise Mixin Extras.
		MixinExtrasBootstrap.init();

		// Register the `@Description` annotation processor for config fields
		ConfigFieldAnnotationProcessor.register(Description.class, new Description.AnnotationProcessor());
	}

	@Override
	public void onInitializeClient(ModContainer mod) {
	}

	public static int searchBoxWidth() {
//		return SearchableConfig.INSTANCE.show_config_button.value() ? SEARCH_BOX_WIDTH - CONFIG_BUTTON_SIZE - CONFIG_BUTTON_OFFSET : SEARCH_BOX_WIDTH;
		return SEARCH_BOX_WIDTH;
	}
}
