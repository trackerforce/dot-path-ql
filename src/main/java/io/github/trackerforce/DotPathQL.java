package io.github.trackerforce;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Utility class for filtering objects based on specified paths.
 * It supports nested properties, collections, and arrays.
 *
 * @author petruki
 * @since 2025-08-02
 */
@SuppressWarnings("unchecked")
public class DotPathQL {

	private final PathCommon pathFilter;
	private final PathCommon pathExclude;

	/**
	 * Constructs a DotPathQL instance with an empty list of default filter paths.
	 */
	public DotPathQL() {
		pathFilter = new PathFilter();
		pathExclude = new PathExclude();
	}

	/**
	 * Filters the given source object based on the specified paths.
	 * The paths can include nested properties, collections, and arrays.
	 * Also supports grouped paths syntax like "parent[child1.prop,child2.prop]"
	 *
	 * @param <T>         the type of the source object
	 * @param source      the source object to filter
	 * @param filterPaths the list of paths to filter
	 * @return a map containing the filtered properties
	 */
	public <T> Map<String, Object> filter(T source, List<String> filterPaths) {
		return pathFilter.run(source, filterPaths);
	}

	/**
	 * Excludes the given paths from the source object and returns the remaining structure.
	 * Works as the inverse of {@link #filter(Object, List)} – instead of selecting only
	 * specific paths, it returns all properties except the excluded ones.
	 * Supports the same grouped path syntax (e.g. "locations[home.street,work.city]").
	 *
	 * @param <T> the type of the source object
	 * @param source the source object to extract from
	 * @param excludePaths list of dot paths to exclude
	 * @return a map containing all properties except the excluded ones
	 */
	public <T> Map<String, Object> exclude(T source, List<String> excludePaths) {
		return pathExclude.run(source, excludePaths);
	}

	/**
	 * Extracts a map from the source map based on the specified property.
	 *
	 * @param source   the source map
	 * @param property the property to extract
	 * @return the extracted map or an empty map if not found
	 * @throws ClassCastException if the property is not a map
	 */
	public Map<String, Object> mapFrom(Map<String, Object> source, String property) {
		if (isInvalid(source, property)) {
			return Collections.emptyMap();
		}

		return (Map<String, Object>) source.get(property);
	}

	/**
	 * Extracts a list of maps from the source map based on the specified property.
	 *
	 * @param source   the source map
	 * @param property the property to extract
	 * @return the extracted list of maps or an empty list if not found
	 * @throws ClassCastException if the property is not a list of maps
	 */
	public List<Map<String, Object>> listFrom(Map<String, Object> source, String property) {
		if (isInvalid(source, property)) {
			return Collections.emptyList();
		}

		return (List<Map<String, Object>>) source.get(property);
	}

	/**
	 * Extracts a list of objects from the source map based on the specified property.
	 *
	 * @param source   the source map
	 * @param property the property to extract
	 * @return the extracted list of objects or an empty list if not found
	 * @throws ClassCastException if the property is not a list of objects
	 */
	public Object[] arrayFrom(Map<String, Object> source, String property) {
		if (isInvalid(source, property)) {
			return new Object[0];
		}

		return (Object[]) source.get(property);
	}

	/**
	 * Adds default filter paths that will be included in every filtering operation.
	 *
	 * @param paths the list of default filter paths to add
	 */
	public void addDefaultFilterPaths(List<String> paths) {
		pathFilter.addDefaultPaths(paths);
	}

	/**
	 * Adds default exclude paths that will be included in every exclusion operation.
	 *
	 * @param paths the list of default exclude paths to add
	 */
	public void addDefaultExcludePaths(List<String> paths) {
		pathExclude.addDefaultPaths(paths);
	}

	private boolean isInvalid(Map<String, Object> source, String property) {
		return source == null || property == null || property.isEmpty() || !source.containsKey(property);
	}
}
