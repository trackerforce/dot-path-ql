package ca.trackerforce;

import ca.trackerforce.fixture.clazz.customer.Customer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObfuscateTypeClassTest {

	DotPathQL dotPathQL = new DotPathQL();

	@Test
	void shouldObfuscateAndNotReturnPrivateAttributes() {
		// Given
		var customer = Customer.of();

		// When
		var result = dotPathQL.obfuscate(customer, List.of("email"));

		// Then
		var metadata = DotUtils.mapFrom(result, "metadata"); // private field
		assertEquals(0, metadata.size());

		var email = result.get("email");
		assertEquals("****", email);
	}
}
