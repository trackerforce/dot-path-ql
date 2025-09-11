package ca.trackerforce.path.api;

import java.util.List;

/**
 * Interface for parsing dot path strings into a list of path segments.
 */
public interface DotParse {

	/**
	 * Parses a dot path string into a list of path segments.
	 *
	 * @param path the dot path string to parse
	 * @return a list of path segments extracted from the dot path
	 */
	List<String> parse(String path);
}
