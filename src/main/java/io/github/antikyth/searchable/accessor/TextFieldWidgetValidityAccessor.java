package io.github.antikyth.searchable.accessor;

import java.util.Optional;
import java.util.regex.PatternSyntaxException;

public interface TextFieldWidgetValidityAccessor {
	void searchable$setValidity(Optional<PatternSyntaxException> validityError);

	Optional<PatternSyntaxException> searchable$getValidityError();
}
