package io.github.trackerforce;

import io.github.trackerforce.fixture.clazz.customer.Customer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FilterTypeClassTest {

	DotPathQL dotPathQL = new DotPathQL();

	@Test
	void shouldReturnFilteredObjectPrivateAttributes() {
		// Given
		var customer = Customer.of();

		// When
		var result = dotPathQL.filter(customer, List.of(
				"metadata.password",
				"metadata.tags"
		));

		// Then
		assertEquals(1, result.size());

		// Verify metadata structure
		var metadata = DotUtils.mapFrom(result, "metadata");

		assertNotNull(metadata);
		assertEquals("securePassword", metadata.get("password"));

		assertNotNull(metadata.get("tags"));
		var tags = DotUtils.arrayFrom(metadata, "tags");
		assertEquals(2, tags.length);
		assertEquals("tag1", tags[0]);
		assertEquals("tag2", tags[1]);
	}

	@Test
	void shouldReturnFilteredObjectListOfListAttributes() {
		// Given
		var customer = Customer.of();

		// When
		var result = dotPathQL.filter(customer, List.of("features.metadata.tags"));

		// Then
		assertEquals(1, result.size());
		assertNotNull(result.get("features"));
		var features = DotUtils.listFrom(result, "features");
		assertNotNull(features);

		assertEquals(1, features.size());
		var metadata = DotUtils.listFrom(features.get(0), "metadata");
		assertNotNull(metadata);

		assertEquals(2, metadata.size());
		assertEquals("defaultTag", DotUtils.arrayFrom(metadata.get(0), "tags")[0]);
		assertEquals("anotherTag", DotUtils.arrayFrom(metadata.get(1), "tags")[0]);
	}
}
