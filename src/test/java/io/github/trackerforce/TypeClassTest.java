package io.github.trackerforce;

import io.github.trackerforce.fixture.clazz.customer.Customer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TypeClassTest {

	DotPathQL dotPathQL = new DotPathQL();

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
		var tags = dotPathQL.arrayFrom(metadata, "tags");
		assertEquals(2, tags.length);
		assertEquals("tag1", tags[0]);
		assertEquals("tag2", tags[1]);
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
		var metadata = dotPathQL.listFrom(features.get(0), "metadata");
		assertNotNull(metadata);

		assertEquals(2, metadata.size());
		assertEquals("defaultTag", dotPathQL.arrayFrom(metadata.get(0), "tags")[0]);
		assertEquals("anotherTag", dotPathQL.arrayFrom(metadata.get(1), "tags")[0]);
	}
}
