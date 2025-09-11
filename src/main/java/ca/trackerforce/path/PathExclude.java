package ca.trackerforce.path;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

class PathExclude extends PathCommon {

	private enum SkipValue {
		INSTANCE
	}

	public <T> Map<String, Object> execute(T source, List<String> excludePaths) {
		Map<String, Object> result = new LinkedHashMap<>();
		excludePaths.addAll(0, defaultPaths);

		ExclusionNode root = buildExclusionTree(excludePaths);
		buildExcluding(result, source, "", root);
		return result;
	}

	private ExclusionNode buildExclusionTree(List<String> paths) {
		ExclusionNode root = new ExclusionNode();

		for (String path : paths) {
			if (path == null || path.isBlank()) continue;
			String[] parts = path.split("\\.");
			ExclusionNode current = root;
			for (int i = 0; i < parts.length; i++) {
				String p = parts[i];
				current = current.getChildren().computeIfAbsent(p, k -> new ExclusionNode());
				if (i == parts.length - 1) {
					current.setExcludeSelf(true);
				}
			}
		}

		return root;
	}

	private void buildExcluding(Map<String, Object> target, Object source, String currentPath, ExclusionNode node) {
		if (source == null || isSimpleValue(source)) {
			return;
		}

		if (source instanceof Map<?, ?> map) {
			excludeFromMap(target, currentPath, node, map);
			return;
		}

		for (String prop : getPropertyNames(source.getClass())) {
			ExclusionNode childNode = node == null ? null : node.getChildren().get(prop);
			if (childNode != null && childNode.isExcludeSelf() && childNode.getChildren().isEmpty()) {
				continue;
			}

			Object value = getPropertyValue(source, prop);
			String path = currentPath.isEmpty() ? prop : currentPath + "." + prop;
			Object built = buildValueExcluding(value, path, childNode);
			if (built != SkipValue.INSTANCE) {
				target.put(prop, built);
			}
		}
	}

	private void excludeFromMap(Map<String, Object> target, String currentPath, ExclusionNode node, Map<?, ?> map) {
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			String key = String.valueOf(entry.getKey());
			ExclusionNode childNode = node == null ? null : node.getChildren().get(key);

			if (childNode != null && childNode.isExcludeSelf() && childNode.getChildren().isEmpty()) {
				continue;
			}

			Object value = entry.getValue();
			String path = currentPath.isEmpty() ? key : currentPath + "." + key;
			Object built = buildValueExcluding(value, path, childNode);
			if (built != SkipValue.INSTANCE) {
				target.put(key, built);
			}
		}
	}

	private Object buildValueExcluding(Object value, String path, ExclusionNode node) {
		if (isSimpleValue(value)) {
			return value;
		}

		if (value instanceof Map<?, ?> mapVal) {
			Map<String, Object> nested = new LinkedHashMap<>();
			buildExcluding(nested, mapVal, path, node == null ? new ExclusionNode() : node);
			return nested;
		}

		if (value instanceof Collection<?> || value.getClass().isArray()) {
			return handleCollectionOrArray(value, path, node);
		}

		Map<String, Object> nested = new LinkedHashMap<>();
		buildExcluding(nested, value, path, node == null ? new ExclusionNode() : node);
		return nested;
	}

	private Object handleCollectionOrArray(Object value, String path, ExclusionNode node) {
		Object[] array = null;
		List<?> list;
		boolean isArray = value.getClass().isArray();

		if (isArray) {
			if (isPrimitiveArray(value)) {
				return value;
			}

			array = (Object[]) value;
			list = Arrays.asList((Object[]) value);
		} else {
			list = new ArrayList<>((Collection<?>) value);
		}

		boolean allSimple = list.stream().allMatch(this::isSimpleValue);
		if (allSimple && (node == null || node.getChildren().isEmpty())) {
			return isArray ? array : list;
		}

		return addElementsToList(path, node, list);
	}

	private List<Object> addElementsToList(String path, ExclusionNode node, List<?> list) {
		List<Object> items = new ArrayList<>();
		for (Object element : list) {
			if (isSimpleValue(element)) {
				items.add(element);
			} else {
				Map<String, Object> elementMap = new LinkedHashMap<>();
				buildExcluding(elementMap, element, path, node == null ? new ExclusionNode() : node);
				items.add(elementMap);
			}
		}
		return items;
	}

	private boolean isSimpleValue(Object value) {
		return value == null || value instanceof String || value instanceof Number || value instanceof Boolean ||
				value instanceof Character || value instanceof Enum<?> || value instanceof java.util.Date ||
				value.getClass().isPrimitive();
	}

	private boolean isPrimitiveArray(Object value) {
		return value instanceof boolean[] || value instanceof byte[] || value instanceof char[] ||
				value instanceof short[] || value instanceof int[] || value instanceof long[] ||
				value instanceof float[] || value instanceof double[];
	}

	private List<String> getPropertyNames(Class<?> clazz) {
		List<String> names = new ArrayList<>();
		if (clazz.isRecord()) {
			Arrays.stream(clazz.getRecordComponents()).forEach(rc -> names.add(rc.getName()));
		} else {
			for (Field f : clazz.getDeclaredFields()) {
				if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
					continue;
				}

				if (hasAccessibleGetter(clazz, f)) {
					names.add(f.getName());
				}
			}
		}
		return names;
	}

	private boolean hasAccessibleGetter(Class<?> clazz, Field field) {
		String fieldName = field.getName();
		String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		String booleanGetterName = "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

		return isMethodAccessible(clazz, field, getterName) ||
				isMethodAccessible(clazz, field, booleanGetterName) ||
				isMethodAccessible(clazz, field, fieldName);
	}

	private boolean isMethodAccessible(Class<?> clazz, Field field, String getterName) {
		try {
			Method getter = clazz.getMethod(getterName);
			return getter.getReturnType().equals(field.getType()) && getter.getParameterCount() == 0;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}
}
