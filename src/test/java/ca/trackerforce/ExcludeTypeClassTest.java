package ca.trackerforce;

import ca.trackerforce.fixture.clazz.customer.Customer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcludeTypeClassTest {

	DotPathQL dotPathQL = new DotPathQL();

	@Test
	void shouldExcludeAndNotReturnPrivateAttributes() {
		// Given
		var customer = Customer.of();

		// When
		var result = dotPathQL.exclude(customer, List.of("metadata.tags"));

		// Then
		var metadata = DotUtils.mapFrom(result, "metadata");
		assertEquals(0, metadata.size()); // No fields should remain after excluding tags and private password

		var features = DotUtils.listFrom(result, "features");
		assertEquals(2, features.size());
		assertTrue(features.get(0).containsKey("isEnabled")); // checking boolean field
	}
}
