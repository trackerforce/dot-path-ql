package io.github.trackerforce.fixture.clazz;

import lombok.Data;

import java.util.List;

@Data
public class Feature {
	private String name;
	private String description;
	private boolean isEnabled;
	private List<Metadata> metadata;
}
