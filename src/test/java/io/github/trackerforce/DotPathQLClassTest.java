package io.github.trackerforce;

import io.github.trackerforce.fixture.clazz.Customer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("unchecked")
class DotPathQLClassTest {

	DotPathQL dotPathQL = new DotPathQL();

	@Test
	void shouldReturnFilteredObjectAttributes() {
		// Given
		var customer = Customer.of();
		var filterPaths = List.of(
			"name",
			"features.name"
		);

		// When
		var result = dotPathQL.filter(customer, filterPaths);

		// Then
		assertEquals(2, result.size());
		assertEquals("Default Name", result.get("name"));

		// Verify nested features structure
		var features = dotPathQL.listFrom(result, "features");
		assertNotNull(features);
		assertEquals(2, features.size());
		assertEquals("Default Feature", features.get(0).get("name"));
		assertEquals("Additional Feature", features.get(1).get("name"));
	}

	@Test
	void shouldReturnFilteredObjectPrivateAttributes() {
		// Given
		var customer = Customer.of();
		var filterPaths = List.of(
				"metadata.password",
				"metadata.tags"
		);

		// When
		var result = dotPathQL.filter(customer, filterPaths);

		// Then
		assertEquals(1, result.size());

		// Verify metadata structure
		var metadata = dotPathQL.mapFrom(result, "metadata");

		assertNotNull(metadata);
		assertEquals("securePassword", metadata.get("password"));

		assertNotNull(metadata.get("tags"));
		assertEquals(2, ((String[]) metadata.get("tags")).length);
		assertEquals("tag1", ((String[]) metadata.get("tags"))[0]);
		assertEquals("tag2", ((String[]) metadata.get("tags"))[1]);
	}

	@Test
	void shouldReturnFilteredObjectListOfListAttributes() {
		// Given
		var customer = Customer.of();
		var filterPaths = List.of("features.metadata.tags");

		// When
		var result = dotPathQL.filter(customer, filterPaths);

		// Then
		assertEquals(1, result.size());
		assertNotNull(result.get("features"));
		var features = dotPathQL.listFrom(result, "features");
		assertNotNull(features);

		assertEquals(1, features.size());
		var metadata = (List<Object>) features.get(0).get("metadata");
		assertNotNull(metadata);

		assertEquals(2, metadata.size());
		assertEquals("defaultTag", ((String[]) ((Map<String, Object>) metadata.get(0)).get("tags"))[0]);
		assertEquals("anotherTag", ((String[])((Map<String, Object>) metadata.get(1)).get("tags"))[0]);

	}
}
