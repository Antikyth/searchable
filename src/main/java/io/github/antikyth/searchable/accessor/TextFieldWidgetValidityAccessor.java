package io.github.antikyth.searchable.accessor;

import org.jetbrains.annotations.Nullable;

import java.util.regex.PatternSyntaxException;

public interface TextFieldWidgetValidityAccessor {
	void searchable$setValidity(@Nullable PatternSyntaxException validityError);
}
