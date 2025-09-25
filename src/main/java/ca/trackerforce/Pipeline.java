package ca.trackerforce;

import ca.trackerforce.path.api.DotPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Pipeline for chaining multiple operations on an object using a fluent API.
 *
 * @author petruki
 * @since 2025-09-24
 */
public class Pipeline<T> {

    private final T source;
    private final DotPath pathExclude;
    private final DotPath pathObfuscate;
    private final List<String> excludePaths;
    private final List<String> obfuscatePaths;

    /**
     * Creates a new Pipeline instance for the given source object.
     *
     * @param source the source object to be processed by the pipeline operations
     * @param pathExclude the DotPath instance for exclude operations from DotPathQL
     * @param pathObfuscate the DotPath instance for obfuscate operations from DotPathQL
     */
    public Pipeline(T source, DotPath pathExclude, DotPath pathObfuscate) {
        this.source = source;
        this.pathExclude = pathExclude;
        this.pathObfuscate = pathObfuscate;
        this.excludePaths = new ArrayList<>();
        this.obfuscatePaths = new ArrayList<>();
    }

    /**
     * Adds paths to be excluded from the final result.
     *
     * @param paths the list of paths to exclude
     * @return this Pipeline instance for method chaining
     */
    public Pipeline<T> exclude(List<String> paths) {
        this.excludePaths.addAll(paths);
        return this;
    }

	/**
	 * Triggers exclusion with no paths. Used with default exclude paths if any.
	 *
	 * @return this Pipeline instance for method chaining
	 */
	public Pipeline<T> exclude() {
		return exclude(List.of());
	}

    /**
     * Adds paths to be obfuscated in the final result.
     *
     * @param paths the list of paths to obfuscate
     * @return this Pipeline instance for method chaining
     */
    public Pipeline<T> obfuscate(List<String> paths) {
        this.obfuscatePaths.addAll(paths);
        return this;
    }

	/**
	 * Triggers obfuscation with no paths. Used with default obfuscate paths if any.
	 *
	 * @return this Pipeline instance for method chaining
	 */
	public Pipeline<T> obfuscate() {
		return obfuscate(List.of());
	}

    /**
     * Executes the pipeline operations and returns the final result.
     *
     * @return a map containing the processed object
     */
    public Map<String, Object> execute() {
        Map<String, Object> result = pathExclude.run(source, excludePaths);

		if (pathObfuscate.hasDefaultPaths() || !obfuscatePaths.isEmpty()) {
			return pathObfuscate.run(result, obfuscatePaths);
		}

        return result;
    }
}
