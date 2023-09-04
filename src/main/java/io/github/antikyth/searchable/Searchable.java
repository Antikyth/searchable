/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable;

import io.github.antikyth.searchable.config.SearchableConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Searchable implements ClientModInitializer {
	public static final String NAME = "Searchable";
	public static final String MOD_ID = "searchable";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	public static SearchableConfig config;

	@Override
	public void onInitializeClient(ModContainer mod) {
		// Register config.
		AutoConfig.register(SearchableConfig.class, Toml4jConfigSerializer::new);
		// Load config.
		Searchable.config = AutoConfig.getConfigHolder(SearchableConfig.class).getConfig();
	}
}
