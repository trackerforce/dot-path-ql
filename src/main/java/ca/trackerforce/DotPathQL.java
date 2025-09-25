package ca.trackerforce;

import ca.trackerforce.path.DotPathFactory;
import ca.trackerforce.path.api.DotPath;
import ca.trackerforce.path.api.DotPrinter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * API for filtering and excluding properties from objects using dot paths.
 *
 * @author petruki
 * @since 2025-08-02
 */
public class DotPathQL {

	private final DotPath pathFilter;
	private final DotPath pathExclude;
	private final DotPath pathObfuscate;
	private final DotPrinter pathPrinter;

	/**
	 * Constructs a DotPathQL instance with an empty list of default filter paths.
	 */
	public DotPathQL() {
		pathFilter = DotPathFactory.buildFilter();
		pathExclude = DotPathFactory.buildExclude();
		pathObfuscate = DotPathFactory.buildObfuscate();
		pathPrinter = DotPathFactory.buildPrinter(2);
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
	 * Obfuscates the given source object based on the specified paths.
	 * The paths can include nested properties, collections, and arrays.
	 * Also supports grouped paths syntax like "parent[child1.prop,child2.prop]"
	 *
	 * @param <T>         the type of the source object
	 * @param source      the source object to obfuscate
	 * @param obfuscatePaths the list of paths to obfuscate
	 * @return a map containing the obfuscated properties
	 */
	public <T> Map<String, Object> obfuscate(T source, List<String> obfuscatePaths) {
		return pathObfuscate.run(source, obfuscatePaths);
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

	/**
	 * Adds default obfuscate paths that will be included in every obfuscation operation.
	 *
	 * @param paths the list of default obfuscate paths to add
	 */
	public void addDefaultObfuscatePaths(List<String> paths) {
		pathObfuscate.addDefaultPaths(paths);
	}

	/**
	 * Converts the source object to a map representation.
	 *
	 * @param <T> the type of the source object
	 * @param source the source object to convert
	 * @return a map containing all properties of the source object
	 */
	public <T> Map<String, Object> toMap(T source) {
		return exclude(source, Collections.emptyList());
	}

	/**
	 * Converts the given sourceMap to a JSON string representation with optional formatting.
	 *
	 * @param sourceMap the source map to convert to JSON
	 * @param indentSize the number of spaces to use for indentation
	 * @return a JSON string representation of the object
	 */
	public String toJson(Map<String, Object> sourceMap, int indentSize) {
		pathPrinter.setIndentSize(indentSize);
		return toJson(sourceMap, true);
	}

	/**
	 * Converts the given sourceMap to a JSON string representation.
	 *
	 * @param sourceMap the source map to convert to JSON
	 * @param prettier if true, formats with proper indentation; if false, compact single-line format
	 * @return a JSON string representation of the object
	 */
	public String toJson(Map<String, Object> sourceMap, boolean prettier) {
		return pathPrinter.toJson(sourceMap, prettier);
	}

}
