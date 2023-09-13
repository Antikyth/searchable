/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.datagen.annotation;

import io.github.antikyth.searchable.datagen.annotation.processor.DataGenProcessor;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import org.quiltmc.config.api.ReflectiveConfig;

import java.lang.annotation.*;

/**
 * Provides the translation key for a translated string in a {@link FabricLanguageProvider}.
 * <p>
 * A {@link DataGenProcessor} must be {@linkplain DataGenProcessor#create created} so that {@link DataGenProcessor#addAll}
 * can be called in the {@link FabricLanguageProvider#generateTranslations generateTranslations(TranslationBuilder)}
 * implementation.
 * <h2>Examples</h2>
 * <pre>{@code
 * public class EnglishUs extends FabricLanguageProvider {
 *     private final DataGenProcessor<EnglishUs> processor = DataGenProcessor.create(
 *         this,              // instance of the language provider
 *         EnglishUs.class,   // the language provider class
 *         Searchable.MOD_ID  // the lang namespace
 *     );
 *
 *     public EnglishUs(FabricDataOutput dataOutput) {}
 *         super(dataOutput, "en_us");
 *     }
 *
 *     @Translation("option.language.search")
 *     public static final String LANGUAGE_SEARCH_NARRATION = "search for multiplayer servers";
 *
 *     // The first of any `%s`s is replaced with the namespace, so in this case it will end up being
 *     // `config.searchable.title`.
 *     @Translation("config.%s.title")
 *     public static final String CONFIG_TITLE = "Searchable Options";
 *
 *     @Overrride
 *     public void generateTranslations(TranslationBuilder translationBuilder) {
 *         this.processor.addAll(translationBuilder);
 *     }
 * }
 * }</pre>
 */
@Target(ElementType.FIELD)
@Repeatable(Translations.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Translation {
	/**
	 * The translation key for this translation.
	 * <p>
	 * The first {@code %s} will be replaced with the namespace provided to {@link DataGenProcessor#create}.
	 */
	String value();

	/**
	 * Provides the technical name for a {@link ReflectiveConfig.Section}.
	 * <p>
	 * This should be used on a {@code static} inner class of the {@link FabricLanguageProvider} documented for
	 * {@link Translation}. On its own, it won't achieve anything, but fields within that inner class annotated with one
	 * of the following annotations make use of that technical name:
	 * <ul>
	 *     <li>
	 *         <h3>{@link Translation.ConfigCategory.Name}</h3>
	 *         Must be within one of these {@code static} inner classes annotated with
	 *         {@link Translation.ConfigCategory}.
	 *     </li>
	 *     <li>
	 *         <h3>{@link Translation.ConfigCategory.Description}</h3>
	 *         Must be within one of these {@code static} inner classes annotated with
	 *         {@link Translation.ConfigCategory}.
	 *     </li>
	 *     <li>
	 *         <h3>{@link Translation.ConfigOption.Name}</h3>
	 *         Will have the category's technical name prepended to its technical name.
	 *     </li>
	 *     <li>
	 *         <h3>{@link Translation.ConfigOption.Description}</h3>
	 *         Will have the category's technical name prepended to its technical name.
	 *     </li>
	 * </ul>
	 * <p>
	 * <h2>Examples</h2>
	 * <pre>{@code
	 * public class EnglishUs extends FabricLanguageProvider {
	 *     // snippet - see `Translation`'s JavaDocs for the `EnglishUs` class example
	 *
	 *     @Translation.ConfigCategory("select_server_screen")
	 *     public static class SelectServerScreen {
	 *         // Translation key, if the namespace is `searchable`, will be:
	 *         // `config.searchable.category.select_server_screen`
	 *         @Translation.ConfigCategory.Name
	 *         public static final String NAME = "Select Server Screen";
	 *         // Translation key, if the namespace is `searchable`, will be:
	 *         // `config.searchable.category.select_server_screen.description`
	 *         @Translation.ConfigCategory.Description
	 *         public static final String DESCRIPTION = "The multiplayer server selection screen.";
	 *
	 *         // Translation key, if the namespace is `searchable`, will be:
	 *         // `config.searchable.option.select_server_screen.add_search`
	 *         @Translation.ConfigOption.Name("add_search")
	 *         public static final String ADD_SEARCH = EnglishUs.ADD_SEARCH;
	 *
	 *         // Translation key, if the namespace is `searchable`, will be:
	 *         // `config.searchable.option.select_server_screen.match_motds`
	 *         @Translation.ConfigOption.Name("match_motds")
	 *         public static final String MATCH_MOTDS = "Match MOTDs";
	 *         // Translation key, if the namespace is `searchable`, will be:
	 *         // `config.searchable.option.select_server_screen.match_motds.description`
	 *         @Translation.ConfigOption.Description("match_motds")
	 *         public static final String MATCH_MOTDS_DESCRIPTION = """
	 *             Whether server descriptions should be searched for matching text during a search.""";
	 *     }
	 *
	 *     // snippet - see `Translation`'s JavaDocs for the `EnglishUs` class example
	 * }
	 * }</pre>
	 */
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@interface ConfigCategory {
		String value();

		@Target(ElementType.FIELD)
		@Retention(RetentionPolicy.RUNTIME)
		@interface Name {
		}

		@Target(ElementType.FIELD)
		@Retention(RetentionPolicy.RUNTIME)
		@interface Description {
		}
	}

	class ConfigOption {
		@Target(ElementType.FIELD)
		@Retention(RetentionPolicy.RUNTIME)
		public @interface Name {
			String value();
		}

		@Target(ElementType.FIELD)
		@Retention(RetentionPolicy.RUNTIME)
		public @interface Description {
			String value();
		}
	}
}
