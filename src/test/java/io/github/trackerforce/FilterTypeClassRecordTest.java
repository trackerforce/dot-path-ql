package io.github.trackerforce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FilterTypeClassRecordTest {

	DotPathQL dotPathQL = new DotPathQL();

	static Stream<Arguments> userDetailProvider() {
		return Stream.of(
				Arguments.of("Record type", io.github.trackerforce.fixture.record.UserDetail.of()),
				Arguments.of("Class type", io.github.trackerforce.fixture.clazz.UserDetail.of())
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldReturnFilteredObjectAttributes(String implementation, Object userDetail) {
		// When
		var result = dotPathQL.filter(userDetail, List.of(
				"username",
				"address.street",
				"orders.products.name"
		));

		// Then
		assertEquals(3, result.size());
		assertEquals("john_doe", result.get("username"));

		// Verify nested address structure
		var address = DotUtils.mapFrom(result, "address");
		assertNotNull(address);
		assertEquals("123 Main St", address.get("street"));

		// Verify nested orders structure with products
		var orders = DotUtils.listFrom(result, "orders");
		assertNotNull(orders);
		assertEquals(2, orders.size());

		// Debug: Print first order
		var firstOrder = orders.get(0);

		// Verify first order with its products
		var firstOrderProducts = DotUtils.listFrom(firstOrder, "products");
		assertNotNull(firstOrderProducts, "Products list should not be null");
		assertEquals(2, firstOrderProducts.size());
		assertEquals("Laptop", firstOrderProducts.get(0).get("name"));
		assertEquals("Smartphone", firstOrderProducts.get(1).get("name"));

		// Verify second order with its product
		var secondOrder = orders.get(1);
		var secondOrderProducts = DotUtils.listFrom(secondOrder, "products");
		assertEquals(1, secondOrderProducts.size());
		assertEquals("Headphones", secondOrderProducts.get(0).get("name"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldReturnFilteredObjectWithArray(String implementation, Object userDetail) {
		// When
		var result = dotPathQL.filter(userDetail, List.of("occupations.title"));

		// Then
		assertEquals(1, result.size());

		// Verify occupations structure
		var occupations = DotUtils.listFrom(result, "occupations");
		assertEquals(2, occupations.size());
		assertEquals("Software Engineer", occupations.get(0).get("title"));
		assertEquals("Project Manager", occupations.get(1).get("title"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldReturnFilteredObjectWithMap(String implementation, Object userDetail) {
		// When
		var result = dotPathQL.filter(userDetail, List.of(
				"additionalInfo.preferredLanguage",
				"additionalInfo.subscriptionStatus"
		));

		// Then
		assertEquals(1, result.size());

		// Verify additionalInfo structure
		var additionalInfo = DotUtils.mapFrom(result, "additionalInfo");
		assertNotNull(additionalInfo);
		assertEquals("English", additionalInfo.get("preferredLanguage"));
		assertEquals("Active", additionalInfo.get("subscriptionStatus"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldReturnFilteredObjectWithComplexMap(String implementation, Object userDetail) {
		// When
		var result = dotPathQL.filter(userDetail, List.of(
				"locations.home.street",
				"locations.work.city"
		));

		// Then
		assertEquals(1, result.size());

		var locations = DotUtils.mapFrom(result, "locations");
		assertEquals(2, locations.size());

		// Verify home address structure
		var homeLocation = DotUtils.mapFrom(locations, "home");
		assertNotNull(homeLocation);
		assertEquals("456 Elm St", homeLocation.get("street"));

		// Verify work address structure
		var workLocation = DotUtils.mapFrom(locations, "work");
		assertNotNull(workLocation);
		assertEquals("Springfield", workLocation.get("city"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldAddDefaultFilterPaths(String implementation, Object userDetail) {
		// When
		dotPathQL.addDefaultFilterPaths(List.of("username"));
		var result = dotPathQL.filter(userDetail, List.of("address.city"));

		// Then
		assertEquals(2, result.size());
		assertEquals("john_doe", result.get("username"));
		assertEquals(1, DotUtils.mapFrom(result, "address").size());
		assertEquals("Springfield", DotUtils.mapFrom(result, "address").get("city"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldReturnFilteredObjectUsingGroupedPaths(String implementation, Object userDetail) {
		// When
		var result = dotPathQL.filter(userDetail, List.of("locations[home.street,work.city]"));

		// Then
		assertEquals(1, result.size());

		var locations = DotUtils.mapFrom(result, "locations");
		assertEquals(2, locations.size());

		// Verify home address structure
		var homeLocation = DotUtils.mapFrom(locations, "home");
		assertNotNull(homeLocation);
		assertEquals("456 Elm St", homeLocation.get("street"));

		// Verify work address structure
		var workLocation = DotUtils.mapFrom(locations, "work");
		assertNotNull(workLocation);
		assertEquals("Springfield", workLocation.get("city"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldReturnFilteredObjectUsingNestedGroupedPaths(String implementation, Object userDetail) {
		// When
		var result = dotPathQL.filter(userDetail, List.of("locations[home[street,city],work[city]]"));

		// Then
		assertEquals(1, result.size());

		var locations = DotUtils.mapFrom(result, "locations");
		assertEquals(2, locations.size());

		// Verify home address structure
		var homeLocation = DotUtils.mapFrom(locations, "home");
		assertNotNull(homeLocation);
		assertEquals("456 Elm St", homeLocation.get("street"));
		assertEquals("Springfield", homeLocation.get("city"));

		// Verify work address structure
		var workLocation = DotUtils.mapFrom(locations, "work");
		assertNotNull(workLocation);
		assertEquals("Springfield", workLocation.get("city"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldReturnEmptyResultInvalidGroupedPaths(String implementation, Object userDetail) {
		// When
		var result = dotPathQL.filter(userDetail, List.of("locations]home[")); // Invalid grouped path

		// Then
		assertEquals(0, result.size());
	}

	@Test
	void shouldReturnEmptyMapWhenSourceIsNull() {
		// When
		var result = dotPathQL.filter(null, List.of("orders.orderId"));

		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
}
