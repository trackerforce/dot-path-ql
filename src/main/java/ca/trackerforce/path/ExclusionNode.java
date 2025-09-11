package ca.trackerforce.path;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
class ExclusionNode {

	private boolean excludeSelf; // If true, this exact path is excluded

	private final Map<String, ExclusionNode> children;

	public ExclusionNode() {
		children = new HashMap<>();
	}

	public boolean isExcludeSelf() {
		return excludeSelf;
	}

	public void setExcludeSelf(boolean excludeSelf) {
		this.excludeSelf = excludeSelf;
	}

	public Map<String, ExclusionNode> getChildren() {
		return children;
	}
}
