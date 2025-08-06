package io.github.trackerforce;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Utility class for filtering objects based on specified paths.
 * It supports nested properties, collections, and arrays.
 *
 * @author petruki
 * @since 2025-08-02
 */
@SuppressWarnings("unchecked")
public class DotPathQL {

	private final List<String> defaultFilterPaths;

	/**
	 * Constructs a DotPathQL instance with an empty list of default filter paths.
	 */
	public DotPathQL() {
		this.defaultFilterPaths = new ArrayList<>();
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
		Map<String, Object> result = new LinkedHashMap<>();
		List<String> expandedPaths = expandGroupedPaths(filterPaths);
		defaultFilterPaths.addAll(expandedPaths);

		for (String path : defaultFilterPaths) {
			addPathToResult(result, source, path);
		}

		return result;
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
		if (source == null || property == null || property.isEmpty() || !source.containsKey(property)) {
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
		if (source == null || property == null || property.isEmpty() || !source.containsKey(property)) {
			return Collections.emptyList();
		}

		return (List<Map<String, Object>>) source.get(property);
	}

	/**
	 * Adds default filter paths that will be included in every filtering operation.
	 *
	 * @param paths the list of default filter paths to add
	 */
	public void addDefaultFilterPaths(List<String> paths) {
		defaultFilterPaths.addAll(paths);
	}

	/**
	 * Expands grouped paths like "parent[child1.prop,child2.prop]" into individual paths.
	 *
	 * @param filterPaths the list of paths that may contain grouped syntax
	 * @return a list of expanded individual paths
	 */
	private List<String> expandGroupedPaths(List<String> filterPaths) {
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
	 * Expands a single grouped path into individual paths.
	 *
	 * @param groupedPath the grouped path to expand
	 * @return a list of individual paths
	 */
	private List<String> expandGroupedPath(String groupedPath) {
		List<String> expandedPaths = new ArrayList<>();

		int startBracket = groupedPath.indexOf('[');
		int endBracket = groupedPath.indexOf(']');

		if (startBracket == -1 || endBracket == -1 || startBracket >= endBracket) {
			// Invalid format, return as-is
			expandedPaths.add(groupedPath);
			return expandedPaths;
		}

		String prefix = groupedPath.substring(0, startBracket);
		String groupedContent = groupedPath.substring(startBracket + 1, endBracket);

		// Split by comma and create individual paths
		String[] subPaths = groupedContent.split(",");
		for (String subPath : subPaths) {
			String trimmedSubPath = subPath.trim();
			if (!trimmedSubPath.isEmpty()) {
				expandedPaths.add(prefix + "." + trimmedSubPath);
			}
		}

		return expandedPaths;
	}

	private <T> void addPathToResult(Map<String, Object> result, T source, String path) {
		String[] parts = path.split("\\.", 2);
		String currentProperty = parts[0];
		String remainingPath = parts.length > 1 ? parts[1] : null;

		Object value = getPropertyValue(source, currentProperty);
		if (value == null) {
			return;
		}

		if (remainingPath == null) {
			result.put(currentProperty, value);
		} else {
			extractFromNestedStructure(result, value, currentProperty, remainingPath);
		}
	}

	private void extractFromNestedStructure(Map<String, Object> result, Object value, String currentProperty,
											String remainingPath) {
		// Nested property using Collection
		if (value instanceof Collection<?> collection) {
			List<Map<String, Object>> nestedResults =
					getNestedStructure(result, collection, currentProperty, remainingPath);

			// Remove any empty maps
			nestedResults.removeIf(Map::isEmpty);

			// Nested property using Array
		} else if (value.getClass().isArray()) {
			Object[] array = (Object[]) value;
			List<Map<String, Object>> nestedResults =
					getNestedStructure(result, Arrays.asList(array), currentProperty, remainingPath);

			// Remove any empty maps
			nestedResults.removeIf(Map::isEmpty);

			// Nested property using Map
		} else if (value instanceof Map<?, ?> map) {
			Map<String, Object> nestedResult = (Map<String, Object>)
					result.computeIfAbsent(currentProperty, k -> new LinkedHashMap<>());

			// Split the remaining path to get the next property we're looking for
			String[] remainingParts = remainingPath.split("\\.", 2);
			String targetKey = remainingParts[0];
			String nextRemainingPath = remainingParts.length > 1 ? remainingParts[1] : null;

			// Only process the specific key we're looking for
			if (map.containsKey(targetKey)) {
				Object entryValue = map.get(targetKey);
				if (nextRemainingPath == null) {
					// This is the final property - set the value directly
					nestedResult.put(targetKey, entryValue);
				} else {
					// Continue processing the nested path on the complex object
					// Create a nested map for this key
					Map<String, Object> keyNestedResult = new LinkedHashMap<>();
					nestedResult.put(targetKey, keyNestedResult);
					addPathToResult(keyNestedResult, entryValue, nextRemainingPath);
				}
			}

			// Single nested object - get or create the nested map
		} else {
			Map<String, Object> nestedResult = (Map<String, Object>)
					result.computeIfAbsent(currentProperty, k -> new LinkedHashMap<>());
			result.put(currentProperty, nestedResult);
			addPathToResult(nestedResult, value, remainingPath);
		}
	}

	private List<Map<String, Object>> getNestedStructure(Map<String, Object> result, Collection<?> collection,
														 String currentProperty, String remainingPath) {
		List<Map<String, Object>> nestedResults = (List<Map<String, Object>>)
				result.computeIfAbsent(currentProperty, k -> new ArrayList<>());
		result.put(currentProperty, nestedResults);

		// Ensure we have enough maps in the list for all collection items
		while (nestedResults.size() < collection.size()) {
			nestedResults.add(new LinkedHashMap<>());
		}

		// Process each item in the collection
		for (int index = 0; index < collection.size(); index++) {
			Map<String, Object> nestedMap = nestedResults.get(index);
			addPathToResult(nestedMap, ((List<?>) collection).get(index), remainingPath);
		}

		return nestedResults;
	}

	private <T> Object getPropertyValue(T source, String propertyName) {
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
}
