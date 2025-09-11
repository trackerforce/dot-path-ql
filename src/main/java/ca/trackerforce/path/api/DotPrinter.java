package ca.trackerforce.path.api;

/**
 * Defines common APIs for printing objects to JSON format.
 */
public interface DotPrinter {

	/**
	 * Converts the given object to its JSON representation.
	 *
	 * @param obj the object to convert to JSON
	 * @param prettier if true, the JSON output will be formatted with indentation and line breaks
	 * @return a JSON string representation of the object
	 */
	String toJson(Object obj, boolean prettier);

	/**
	 * Sets the number of spaces to use for indentation in the JSON output.
	 *
	 * @param indentSize the number of spaces for indentation
	 */
	void setIndentSize(int indentSize);
}
