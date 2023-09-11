package io.github.antikyth.searchable.config.metadata;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public @interface FormatArg {
	/**
	 * Either a {@linkplain Type#LITERAL literal string} or a {@linkplain Type#TRANSLATION_KEY translation key} used as
	 * one of the format {@code args} for the {@linkplain Description#value() description}.
	 * <p>
	 * Whether it is a literal or a translation key is determined by the {@link FormatArg#type() type()};
	 */
	String value();

	/**
	 * Whether this arg should be treated as a literal string or a translation key which should first be translated.
	 */
	Type type() default Type.LITERAL;

	/**
	 * A list of {@link Formatting}s that are applied to the format arg {@link Text}.
	 */
	Formatting[] formattings() default {};

	enum Type {
		LITERAL,
		TRANSLATION_KEY
	}
}
