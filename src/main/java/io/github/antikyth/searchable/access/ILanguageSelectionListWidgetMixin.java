package io.github.antikyth.searchable.access;

import net.minecraft.client.gui.screen.option.LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry;
import net.minecraft.client.resource.language.LanguageDefinition;

import java.util.Map;

public interface ILanguageSelectionListWidgetMixin {
    void filter(String query, Map<String, LanguageDefinition> languages);

    LanguageEntry getSelectedLanguage();
}
