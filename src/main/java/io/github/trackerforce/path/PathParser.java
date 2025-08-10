package io.github.trackerforce.path;

import io.github.trackerforce.path.api.DotParse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parser for dot notation paths with support for nested bracket expressions.
 */
public class PathParser implements DotParse {

    /**
     * Parses a dot notation string into a list of paths, handling nested brackets correctly.
     *
     * @param input the dot notation string to parse
     * @return list of parsed paths
     */
    @Override
    public List<String> parse(String input) {
        if (input == null || input.trim().isEmpty()) {
			return Collections.emptyList();
        }

        List<String> paths = new ArrayList<>();
        StringBuilder currentPath = new StringBuilder();
        int bracketDepth = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '[') {
                bracketDepth++;
                currentPath.append(c);
            } else if (c == ']') {
                bracketDepth--;
                currentPath.append(c);
            } else if (c == ',' && bracketDepth == 0) {
                // Only split on comma when not inside brackets
                if (!currentPath.isEmpty()) {
                    paths.add(currentPath.toString().trim());
                    currentPath = new StringBuilder();
                }
            } else {
                currentPath.append(c);
            }
        }

        // Add the last path if there's content
        if (!currentPath.isEmpty()) {
            paths.add(currentPath.toString().trim());
        }

        return paths;
    }
}
