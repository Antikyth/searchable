package io.github.antikyth.searchable;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.antikyth.searchable.config.SearchableConfigScreen;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class SearchableModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return SearchableConfigScreen::new;
	}
}
