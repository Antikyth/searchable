/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.accessor.multiplayer;

import io.github.antikyth.searchable.accessor.SetQueryAccessor;

public interface MultiplayerServerListWidgetAccessor extends SetQueryAccessor {
	String searchable$getQuery();
}