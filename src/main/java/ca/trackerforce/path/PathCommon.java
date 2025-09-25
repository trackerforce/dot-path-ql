package ca.trackerforce.path;

import ca.trackerforce.path.api.DotPath;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Common functionality for handling paths in the DotPathQL library.
 * Provides methods to expand grouped paths, retrieve property values,
 * and manage default filter paths.
 */
abstract class PathCommon implements DotPath {

	/**
	 * Default paths that can be used across different implementations.
	 */
	protected final List<String> defaultPaths;

	/**
	 * Constructor to initialize the PathCommon with an empty list of default paths.
	 * This allows subclasses to add their own default paths as needed.
	 */
	protected PathCommon() {
		this.defaultPaths = new ArrayList<>();
	}

	@Override
	public <T> Map<String, Object> run(T source, List<String> paths) {
		if (source == null) {
			return Collections.emptyMap();
		}

		return execute(source, expandGroupedPaths(paths));
	}

	@Override
	public void addDefaultPaths(List<String> paths) {
		defaultPaths.addAll(paths);
	}

	@Override
	public boolean hasDefaultPaths() {
		return !defaultPaths.isEmpty();
	}

	/**
	 * Executes the path processing logic for the given source object.
	 *
	 * @param <T>         the type of the source object
	 * @param source      the source object to process
	 * @param filterPaths the list of paths to filter or exclude
	 * @return a map containing the processed properties
	 */
	abstract <T> Map<String, Object> execute(T source, List<String> filterPaths);

	/**
	 * Expands grouped paths like "parent[child1.prop,child2.prop]" into individual paths.
	 *
	 * @param filterPaths the list of paths that may contain grouped syntax
	 * @return a list of expanded individual paths
	 */
	protected List<String> expandGroupedPaths(List<String> filterPaths) {
		List<String> expandedPaths = new ArrayList<>();

		for (String path : filterPaths) {
			if (path.contains("[") && path.contains("]")) {
				expandedPaths.addAll(expandGroupedPath(path));
			} else {
				expandedPaths.add(path);
			}
		}

		return expandedPaths;
	}

	/**
	 * Retrieves the value of a property from the given source object.
	 * This method first checks if the source is a record and uses the record component accessor method
	 * if available. If not, it attempts to find a getter method or directly access the field.
	 * If any of these methods fail, it returns null.
	 *
	 * @param source the source object from which to retrieve the property value
	 * @param propertyName the name of the property to retrieve
	 * @return the value of the property, or null if not found or an error occurs
	 * @param <T> the type of the source object
	 */
	protected <T> Object getPropertyValue(T source, String propertyName) {
		try {
			Class<?> clazz = source.getClass();

			// Try record component accessor method first (most efficient for records)
			if (clazz.isRecord()) {
				return getRecordProperty(source, propertyName, clazz);
			}

			// Try getter method for regular classes
			Object getterResult = tryGetterMethod(source, propertyName, clazz);
			if (getterResult != null) {
				return getterResult;
			}

			// Try mapping method for Map instances
			if (source instanceof Map<?, ?> map && map.containsKey(propertyName)) {
				return map.get(propertyName);
			}

			// Fall back to direct field access
			return tryDirectFieldAccess(source, propertyName, clazz);
		} catch (Exception e) {
			return null;
		}
	}

	private <T> Object getRecordProperty(T source, String propertyName, Class<?> clazz) throws
			NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method method = clazz.getMethod(propertyName);
		return method.invoke(source);
	}

	private <T> Object tryGetterMethod(T source, String propertyName, Class<?> clazz) {
		try {
			String getterName = "get" + Character.toUpperCase(propertyName.charAt(0)) +
					propertyName.substring(1);
			Method getter = clazz.getMethod(getterName);
			return getter.invoke(source);
		} catch (Exception e) {
			return null;
		}
	}

	private <T> Object tryDirectFieldAccess(T source, String propertyName, Class<?> clazz) {
		try {
			Field field = clazz.getDeclaredField(propertyName);
			field.setAccessible(true);
			return field.get(source);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Expands a single grouped path into individual paths.
	 * Supports nested brackets like "locations[home[street],work[city]]"
	 *
	 * @param groupedPath the grouped path to expand
	 * @return a list of individual paths
	 */
	private List<String> expandGroupedPath(String groupedPath) {
		List<String> expandedPaths = new ArrayList<>();

		int startBracket = groupedPath.indexOf('[');

		// Find the matching closing bracket by counting bracket depth
		int endBracket = findMatchingClosingBracket(groupedPath, startBracket);
		if (endBracket == -1) {
			expandedPaths.add(groupedPath);
			return expandedPaths;
		}

		String prefix = groupedPath.substring(0, startBracket);
		String groupedContent = groupedPath.substring(startBracket + 1, endBracket);

		// Parse the grouped content, handling nested brackets
		List<String> subPaths = parseGroupedContent(groupedContent);

		for (String subPath : subPaths) {
			if (!subPath.trim().isEmpty()) {
				expandedPaths.add(prefix + "." + subPath.trim());
			}
		}

		return expandedPaths;
	}

	/**
	 * Finds the matching closing bracket for the given opening bracket position.
	 *
	 * @param text the text to search in
	 * @param startPos the position of the opening bracket
	 * @return the position of the matching closing bracket, or -1 if not found
	 */
	private int findMatchingClosingBracket(String text, int startPos) {
		int depth = 1;
		for (int i = startPos + 1; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == '[') {
				depth++;
			} else if (ch == ']') {
				depth--;
				if (depth == 0) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Parses grouped content that may contain nested brackets.
	 * For example: "home[street,city],work[city]" -> ["home[street,city]", "work[city]"]
	 *
	 * @param content the grouped content to parse
	 * @return a list of sub-paths
	 */
	private List<String> parseGroupedContent(String content) {
		List<String> subPaths = new ArrayList<>();
		int depth = 0;
		int start = getStart(content, depth, subPaths);

		// Add the last sub-path
		String lastSubPath = content.substring(start).trim();
		if (!lastSubPath.isEmpty()) {
			// Recursively expand if this subPath contains brackets
			if (lastSubPath.contains("[") && lastSubPath.contains("]")) {
				subPaths.addAll(expandNestedSubPath(lastSubPath));
			} else {
				subPaths.add(lastSubPath);
			}
		}

		return subPaths;
	}

	/**
	 * Gets the start index for parsing comma-separated sub-paths in the content.
	 *
	 * @param content   the content to parse
	 * @param depth     the current bracket depth
	 * @param subPaths  the list to store parsed sub-paths
	 * @return the start index for the next sub-path
	 */
	private int getStart(String content, int depth, List<String> subPaths) {
		int start = 0;

		for (int i = 0; i < content.length(); i++) {
			char ch = content.charAt(i);

			if (ch == '[') {
				depth++;
			} else if (ch == ']') {
				depth--;
			} else if (ch == ',' && depth == 0) {
				// Found a comma at the top level - this is a separator
				String subPath = content.substring(start, i).trim();
				if (!subPath.isEmpty()) {
					if (subPath.contains("[") && subPath.contains("]")) {
						subPaths.addAll(expandNestedSubPath(subPath));
					} else {
						subPaths.add(subPath);
					}
				}
				start = i + 1;
			}
		}
		return start;
	}

	/**
	 * Expands a nested sub-path like "home[street,city]" into "home.street" and "home.city"
	 *
	 * @param subPath the sub-path to expand
	 * @return a list of expanded paths
	 */
	private List<String> expandNestedSubPath(String subPath) {
		List<String> expandedPaths = new ArrayList<>();

		int startBracket = subPath.indexOf('[');
		int endBracket = findMatchingClosingBracket(subPath, startBracket);

		if (startBracket != -1 && endBracket != -1) {
			String prefix = subPath.substring(0, startBracket);
			String nestedContent = subPath.substring(startBracket + 1, endBracket);

			// Parse nested content that may contain its own comma-separated values
			List<String> nestedPaths = parseCommaSeparatedPaths(nestedContent);
			for (String nestedPath : nestedPaths) {
				String trimmed = nestedPath.trim();
				if (!trimmed.isEmpty()) {
					expandedPaths.add(prefix + "." + trimmed);
				}
			}
		}

		return expandedPaths;
	}

	/**
	 * Parses comma-separated paths, respecting bracket nesting.
	 * For example: "street,city" -> ["street", "city"]
	 * For example: "prop1,nested[sub1,sub2]" -> ["prop1", "nested[sub1,sub2]"]
	 *
	 * @param content the content to parse
	 * @return a list of paths
	 */
	private List<String> parseCommaSeparatedPaths(String content) {
		List<String> paths = new ArrayList<>();
		int start = 0;

		for (int i = 0; i < content.length(); i++) {
			char ch = content.charAt(i);

			if (ch == ',') {
				// Found a comma at the top level - this is a separator
				String path = content.substring(start, i).trim();
				if (!path.isEmpty()) {
					paths.add(path);
				}
				start = i + 1;
			}
		}

		// Add the last path
		String lastPath = content.substring(start).trim();
		if (!lastPath.isEmpty()) {
			paths.add(lastPath);
		}

		return paths;
	}

}
