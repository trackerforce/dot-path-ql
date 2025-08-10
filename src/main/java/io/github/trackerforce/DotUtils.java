package io.github.trackerforce;

import io.github.trackerforce.path.DotPathFactory;

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
	public static List<Map<String, Object>> listFrom(Map<String, Object> source, String property) {
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
	public static Object[] arrayFrom(Map<String, Object> source, String property) {
		if (isInvalid(source, property)) {
			return new Object[0];
		}

		return (Object[]) source.get(property);
	}

	private static boolean isInvalid(Map<String, Object> source, String property) {
		return source == null || property == null || property.isEmpty() || !source.containsKey(property);
	}
}


