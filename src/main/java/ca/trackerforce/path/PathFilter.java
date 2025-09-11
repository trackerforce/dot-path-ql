package ca.trackerforce.path;

import java.util.*;

@SuppressWarnings("unchecked")
class PathFilter extends PathCommon {

	public <T> Map<String, Object> execute(T source, List<String> filterPaths) {
		Map<String, Object> result = new LinkedHashMap<>();
		filterPaths.addAll(0, defaultPaths);

		for (String path : filterPaths) {
			addPathToResult(result, source, path);
		}

		return result;
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
			getNestedStructure(result, collection, currentProperty, remainingPath).removeIf(Map::isEmpty);

			// Nested property using Array
		} else if (value.getClass().isArray()) {
			Object[] array = (Object[]) value;
			getNestedStructure(result, Arrays.asList(array), currentProperty, remainingPath).removeIf(Map::isEmpty);

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
					// Get or create a nested map for this key (don't overwrite existing)
					Map<String, Object> keyNestedResult = (Map<String, Object>)
							nestedResult.computeIfAbsent(targetKey, k -> new LinkedHashMap<>());

					addPathToResult(keyNestedResult, entryValue, nextRemainingPath);
				}
			}

			// Single nested object - get or create the nested map
		} else {
			Map<String, Object> nestedResult = (Map<String, Object>)
					result.computeIfAbsent(currentProperty, k -> new LinkedHashMap<>());
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

}
