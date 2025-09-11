package ca.trackerforce.path;

import ca.trackerforce.path.api.DotPrinter;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

class PathPrinter implements DotPrinter {

    private String indent;

	private boolean prettier;

	PathPrinter(int indentSize) {
		setIndentSize(indentSize);
	}

	@Override
    public String toJson(Object obj, boolean prettier) {
		this.prettier = prettier;
        return toJsonInternal(obj, 0);
    }

	@Override
	public void setIndentSize(int indentSize) {
		indent = " ".repeat(indentSize);
	}

    private String toJsonInternal(Object obj, int depth) {
        if (obj == null) {
            return "null";
        }

		if (obj instanceof String value) {
			return "\"" + escapeString(value) + "\"";
		}

		if (obj instanceof Number || obj instanceof Boolean) {
			return obj.toString();
		}

		if (obj instanceof List<?> value) {
			return listToJson(value, depth);
		}

        if (obj instanceof Map<?, ?> value) {
            return mapToJson(value, depth);
        }

        if (obj.getClass().isArray()) {
            return arrayToJson(obj, depth);
        }

        return "\"" + escapeString(obj.toString()) + "\"";
    }

    private String mapToJson(Map<?, ?> map, int depth) {
        if (map.isEmpty()) {
            return "{}";
        }

        if (!prettier) {
            StringJoiner joiner = new StringJoiner(", ", "{", "}");
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = "\"" + escapeString(entry.getKey().toString()) + "\"";
                String value = toJsonInternal(entry.getValue(), depth);
                joiner.add(key + ": " + value);
            }
            return joiner.toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append("\n").append(getIndent(depth + 1));

            String key = "\"" + escapeString(entry.getKey().toString()) + "\"";
            String value = toJsonInternal(entry.getValue(), depth + 1);
            sb.append(key).append(": ").append(value);

            first = false;
        }

        sb.append("\n").append(getIndent(depth)).append("}");
        return sb.toString();
    }

    private String listToJson(List<?> list, int depth) {
        if (list.isEmpty()) {
            return "[]";
        }

        if (!prettier) {
            StringJoiner joiner = new StringJoiner(", ", "[", "]");
            for (Object item : list) {
                joiner.add(toJsonInternal(item, depth));
            }
            return joiner.toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        boolean first = true;
        for (Object item : list) {
            if (!first) {
                sb.append(",");
            }
            sb.append("\n").append(getIndent(depth + 1));
            sb.append(toJsonInternal(item, depth + 1));
            first = false;
        }

        sb.append("\n").append(getIndent(depth)).append("]");
        return sb.toString();
    }

    private String arrayToJson(Object array, int depth) {
        if (Array.getLength(array) == 0) {
            return "[]";
        }

        if (!prettier) {
            StringJoiner joiner = new StringJoiner(", ", "[", "]");
            for (int i = 0; i < Array.getLength(array); i++) {
                joiner.add(toJsonInternal(Array.get(array, i), depth));
            }
            return joiner.toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < Array.getLength(array); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\n").append(getIndent(depth + 1));
            sb.append(toJsonInternal(Array.get(array, i), depth + 1));
        }

        sb.append("\n").append(getIndent(depth)).append("]");
        return sb.toString();
    }

    private String getIndent(int depth) {
        return indent.repeat(depth);
    }

    private String escapeString(String str) {
        if (str == null) {
            return "";
        }

        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
