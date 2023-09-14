/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.datagen.annotation.processor;

import io.github.antikyth.searchable.Searchable;
import io.github.antikyth.searchable.datagen.annotation.Translation;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class TranslationProcessor<P> {
	protected final Class<P> clazz;
	protected final P instance;

	protected final String namespace;
	protected final Map<String, String> translations = new HashMap<>();

	public static <P> TranslationProcessor<P> create(P langProviderInstance, Class<P> langProviderClass, String namespace) {
		TranslationProcessor<P> instance = new TranslationProcessor<>(langProviderClass, langProviderInstance, namespace);
		try {
			instance.process();
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return instance;
	}

	public void addAll(FabricLanguageProvider.TranslationBuilder builder) {
		translations.forEach(builder::add);
	}

	private void process() throws IllegalAccessException {
		this.processFields(clazz, instance, null);
		this.processInnerClasses(clazz, null);
	}

	private void processInnerClasses(Class<?> clazz, @Nullable String categoryKey) throws IllegalAccessException {
		for (Class<?> inner : clazz.getDeclaredClasses()) {
			Translation.ConfigCategory annotation = inner.getAnnotation(Translation.ConfigCategory.class);

			if (Modifier.isStatic(inner.getModifiers())) {
				String key = null;
				if (categoryKey != null) {
					key = annotation == null ? categoryKey : categoryKey + "." + annotation.value();
				} else if (annotation != null) {
					key = annotation.value();
				}

				this.processFields(inner, null, key);
				this.processInnerClasses(inner, key);
			} else if (annotation != null) {
				throw new RuntimeException("Inner class '" + inner.getName()
					+ "' is annotated with @Translation.ConfigCategory but is not static");
			}
		}
	}

	private <T> void processFields(Class<T> clazz, @Nullable T instance, @Nullable String categoryKey) throws IllegalAccessException {
		for (Field field : clazz.getDeclaredFields()) {
			// `@Translation`s {{{
			for (Translation annotation : field.getAnnotationsByType(Translation.class)) {
				Object value = field.get(instance);

				if (value instanceof String translation) {
					String key = format(annotation.value());

					translations.put(key, translation);
				} else {
					throw new RuntimeException(mismatchedTypeMessage(field, "Translation"));
				}
			}
			// }}}

			// `@Translation.ConfigOption.Name` {{{
			Translation.ConfigOption.Name configOptionName = field.getAnnotation(Translation.ConfigOption.Name.class);
			if (configOptionName != null) {
				Object value = field.get(instance);

				if (value instanceof String translation) {
					String key = format("config.%s.option.%s", categoryKey, configOptionName.value());

					translations.put(key, translation);
				} else {
					throw new RuntimeException(mismatchedTypeMessage(field, "Translation.ConfigOption.Name"));
				}
			}
			// }}}

			// `@Translation.ConfigOption.Description` {{{
			Translation.ConfigOption.Description configOptionDescription = field.getAnnotation(Translation.ConfigOption.Description.class);
			if (configOptionDescription != null) {
				Object value = field.get(instance);

				if (value instanceof String translation) {
					String key = format("config.%s.option.%s.description", categoryKey, configOptionDescription.value());

					translations.put(key, translation);
				} else {
					throw new RuntimeException(mismatchedTypeMessage(field, "Translation.ConfigOption.Description"));
				}
			}
			// }}}

			// `@Translation.ConfigCategory.Name` {{{
			Translation.ConfigCategory.Name name = field.getAnnotation(Translation.ConfigCategory.Name.class);
			if (name != null) {
				if (!name.value().isBlank() || categoryKey != null) {
					Object value = field.get(instance);

					if (value instanceof String translation) {
						translations.put(format("config.%s.category.%s", name.value().isBlank() ? categoryKey : name.value()), translation);
					} else {
						throw new RuntimeException(mismatchedTypeMessage(field, "Translation.ConfigCategory.Name"));
					}
				} else {
					throw new RuntimeException(categoryMessage(field, "Name"));
				}
			}
			// }}}

			// `@Translation.ConfigCategory.Description` {{{
			Translation.ConfigCategory.Description description = field.getAnnotation(Translation.ConfigCategory.Description.class);
			if (description != null) {
				if (!description.value().isBlank() || categoryKey != null) {
					Object value = field.get(instance);

					if (value instanceof String translation) {
						translations.put(format("config.%s.category.%s.description", description.value().isBlank() ? categoryKey : description.value()), translation);
					} else {
						throw new RuntimeException(mismatchedTypeMessage(field, "Translation.ConfigCategory.Description"));
					}
				} else {
					throw new RuntimeException(categoryMessage(field, "Description"));
				}
			}
			// }}}
		}
	}

	private static String format(String string) {
		return String.format(string, Searchable.MOD_ID);
	}

	private static String format(String string, String categoryKey) {
		return String.format(string, Searchable.MOD_ID, categoryKey);
	}

	private static String format(String string, @Nullable String categoryKey, String configOption) {
		return String.format(string, Searchable.MOD_ID, (categoryKey == null ? "" : categoryKey + ".") + configOption);
	}

	private static String categoryMessage(Field field, String type) {
		return "Field '" + field.getName() + "' is annotated with @Translation.ConfigCategory." + type
			+ " has no specified technical name nor is within a class annotated with @Translation.ConfigCategory";
	}

	private static String mismatchedTypeMessage(Field field, String annotation) {
		return "Field '" + field.getName() + "' is annotated with @" + annotation + " but is of type '"
			+ field.getType().getName() + "', not 'String'";
	}

	protected TranslationProcessor(Class<P> clazz, P instance, String namespace) {
		this.clazz = clazz;
		this.instance = instance;
		this.namespace = namespace;
	}
}
