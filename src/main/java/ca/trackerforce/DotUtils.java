package ca.trackerforce;

import ca.trackerforce.path.DotPathFactory;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Utility class to complement the DotPathQL functionalities.
 */
@SuppressWarnings("unchecked")
public class DotUtils {

	private DotUtils() { }

	/**
	 * Parses a dot-notated path into its components.
	 *
	 * @param path the dot-notated path
	 * @return a list of path components
	 */
	public static List<String> parsePaths(String path) {
		return DotPathFactory.buildParser().parse(path);
	}

	/**
	 * Extracts a map from the source map based on the specified property.
	 *
	 * @param source   the source map
	 * @param property the property to extract
	 * @return the extracted map or an empty map if not found
	 * @throws ClassCastException if the property is not a map
	 */
	public static Map<String, Object> mapFrom(Map<String, Object> source, String property) {
		Object result = getObjectFromSource(source, property);

		if (!(result instanceof Map)) {
			return Collections.emptyMap();
		}

		return (Map<String, Object>) result;
	}

	/**
	 * Extracts a list of maps from the source map based on the specified property.
	 *
	 * @param source   the source map
	 * @param property the property to extract
	 * @return the extracted list of maps or an empty list if not found
	 * @throws ClassCastException if the property is not a list of maps
	 */
	public static List<Map<String, Object>> listFrom(Map<String, Object> source, String property) {
		Object result = getObjectFromSource(source, property);

		if (!(result instanceof List)) {
			return Collections.emptyList();
		}

		return (List<Map<String, Object>>) result;
	}

	/**
	 * Extracts a typed list from the source map based on the specified property.
	 *
	 * @param source   the source map
	 * @param property the property to extract or a dot-notated path for nested properties
	 * @param clazz    the class type of the list elements
	 * @param <T> the type of the list elements
	 * @return the extracted list of maps or an empty list if not found
	 * @throws ClassCastException if the property is not a list of maps
	 */
	public static <T> List<T> listFrom(Map<String, Object> source, String property, Class<T> clazz) {
		Object result = getObjectFromSource(source, property);

		if (!(result instanceof List) || ((List<?>) result).isEmpty() || !clazz.isInstance(((List<?>) result).get(0))) {
			return Collections.emptyList();
		}

		return (List<T>) result;
	}

	/**
	 * Extracts a list of objects from the source map based on the specified property.
	 *
	 * @param source   the source map
	 * @param property the property to extract or a dot-notated path for nested properties
	 * @return the extracted list of objects or an empty list if not found
	 * @throws ClassCastException if the property is not a list of objects
	 */
	public static Object[] arrayFrom(Map<String, Object> source, String property) {
		Object result = getObjectFromSource(source, property);

		if (result == null || !result.getClass().isArray()) {
			return new Object[0];
		}

		return convertToObjectArray(result);
	}

	private static Object getObjectFromSource(Map<String, Object> source, String property) {
		if (property.contains(".")) {
			String[] keys = property.split("\\.");

			for (int i = 0; i < keys.length - 1; i++) {
				source = (Map<String, Object>) source.get(keys[i]);
			}

			return source.get(keys[keys.length - 1]);
		}

		if (source == null || !source.containsKey(property)) {
			return null;
		}

		return source.get(property);
	}

	private static Object[] convertToObjectArray(Object array) {
		Class<?> componentType = array.getClass().getComponentType();

		if (!componentType.isPrimitive()) {
			return (Object[]) array;
		}

		int length = Array.getLength(array);
		Object[] result = new Object[length];

		for (int i = 0; i < length; i++) {
			result[i] = Array.get(array, i);
		}

		return result;
	}
}
