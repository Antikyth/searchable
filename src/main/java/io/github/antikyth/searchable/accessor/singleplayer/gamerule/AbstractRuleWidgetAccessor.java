package io.github.antikyth.searchable.accessor.singleplayer.gamerule;

import io.github.antikyth.searchable.accessor.MatchesAccessor;
import io.github.antikyth.searchable.accessor.SetQueryAccessor;

public interface AbstractRuleWidgetAccessor extends SetQueryAccessor, MatchesAccessor {
	void searchable$setTechnicalName(String technicalName);
}
