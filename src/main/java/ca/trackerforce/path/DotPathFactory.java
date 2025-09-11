package ca.trackerforce.path;

import ca.trackerforce.path.api.DotParse;
import ca.trackerforce.path.api.DotPath;
import ca.trackerforce.path.api.DotPrinter;

/**
 * Factory class for creating instances of DotPath and DotPrinter implementations.
 */
public class DotPathFactory {

	private DotPathFactory() { }

	/**
	 * Builds and returns a new instance of PathFilter.
	 *
	 * @return a new PathFilter instance
	 */
	public static DotPath buildFilter() {
		return new PathFilter();
	}

	/**
	 * Builds and returns a new instance of PathExclude.
	 *
	 * @return a new PathExclude instance
	 */
	public static DotPath buildExclude() {
		return new PathExclude();
	}

	/**
	 * Builds and returns a new instance of PathPrinter with the specified indentation size.
	 *
	 * @param indentSize the number of spaces to use for indentation
	 * @return a new PathPrinter instance
	 */
	public static DotPrinter buildPrinter(int indentSize) {
		return new PathPrinter(indentSize);
	}

	/**
	 * Builds and returns a new instance of PathParser.
	 *
	 * @return a new PathParser instance
	 */
	public static DotParse buildParser() {
		return new PathParser();
	}
}
