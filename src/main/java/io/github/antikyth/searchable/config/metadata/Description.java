package io.github.antikyth.searchable.config.metadata;

import io.github.antikyth.searchable.config.screen.SearchableConfigScreen.AbstractConfigOptionEntry;
import io.github.antikyth.searchable.config.screen.SearchableConfigScreen.CategoryEntry;
import io.github.antikyth.searchable.util.Pair;
import net.minecraft.text.Text;
import org.quiltmc.config.api.ReflectiveConfig.Section;
import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;
import org.quiltmc.config.api.metadata.MetadataContainerBuilder;
import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.api.values.TrackedValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Optional;

/**
 * Defines the translation key of a description used in tooltips for {@link CategoryEntry}s and
 * {@link AbstractConfigOptionEntry}s.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
	MetadataType<Pair<String, Text[]>, Builder> TYPE = MetadataType.create(Optional::empty, Builder::new);

	/**
	 * The translation key for the description.
	 * <p>
	 * Should follow the following format:
	 * <pre>{@code
	 * config.searchable.<option|category>.<key>
	 * }</pre>
	 * {@code option} is used for {@link TrackedValue}s, while {@code category} is used for
	 * {@link Section}s.
	 */
	String value() default "";

	/**
	 * Any {@code args} to be used to format the description.
	 */
	FormatArg[] args() default {};

	final class AnnotationProcessor implements ConfigFieldAnnotationProcessor<Description> {
		@Override
		public void process(Description annotation, MetadataContainerBuilder<?> builder) {
			builder.metadata(TYPE, _builder -> {
				_builder.setTranslationKey(annotation.value());

				_builder.setArgs(Arrays.stream(annotation.args()).map(arg -> switch (arg.type()) {
					case LITERAL -> Text.literal(arg.value()).formatted(arg.formattings());
					case TRANSLATION_KEY -> Text.translatable(arg.value()).formatted(arg.formattings());
				}).toArray(Text[]::new));
			});
		}
	}

	final class Builder implements MetadataType.Builder<Pair<String, Text[]>> {
		private String translationKey;
		private Text[] args;

		public void setTranslationKey(String translationKey) {
			this.translationKey = translationKey;
		}

		public void setArgs(Text... args) {
			this.args = args;
		}

		@Override
		public Pair<String, Text[]> build() {
			return new Pair<>(this.translationKey, this.args);
		}
	}
}
