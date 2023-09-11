/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.util.match;

/**
 * A query match within a string.
 *
 * @param startIndex The index that the match starts at.
 * @param endIndex   The index that the match ends at (i.e. {@code startIndex + query.length()}).
 */
public record Match(int startIndex, int endIndex) {
}
