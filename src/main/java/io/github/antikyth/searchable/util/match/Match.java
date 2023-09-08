package io.github.antikyth.searchable.util.match;

/**
 * A query match within a string.
 *
 * @param startIndex The index that the match starts at.
 * @param endIndex   The index that the match ends at (i.e. {@code startIndex + query.length()}).
 */
public record Match(int startIndex, int endIndex) {
}
