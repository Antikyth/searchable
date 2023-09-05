package io.github.antikyth.searchable.accessor.singleplayer.gamerule;

import io.github.antikyth.searchable.accessor.SetQueryAccessor;

public interface AbstractRuleWidgetAccessor extends SetQueryAccessor {
	boolean searchable$matches(String query);

	void searchable$setRuleName(String name);
}
