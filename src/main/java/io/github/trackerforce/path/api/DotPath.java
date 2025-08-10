package io.github.trackerforce.path.api;

import java.util.List;
import java.util.Map;

/**
 * Defines common APIs for path processing
 */
public interface DotPath {

	/**
	 * Runs the path processing logic for the given source object with the specified paths.
	 *
	 * @param <T>          the type of the source object
	 * @param source       the source object to process
	 * @param paths		   the list of paths to exclude
	 * @return a map containing the processed properties
	 */
	<T> Map<String, Object> run(T source, List<String> paths);

	/**
	 * Adds default paths to the list of paths that can be used
	 * when processing objects.
	 *
	 * @param paths the list of paths to add as default paths
	 */
	void addDefaultPaths(List<String> paths);
}
