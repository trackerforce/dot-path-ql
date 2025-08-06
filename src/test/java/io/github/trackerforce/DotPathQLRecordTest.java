package io.github.trackerforce;

import io.github.trackerforce.fixture.record.UserDetail;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DotPathQLRecordTest {

	DotPathQL dotPathQL = new DotPathQL();

	@Test
	void shouldReturnFilteredObjectAttributes() {
		// Given
		var userDetail = UserDetail.of();
		var filterPaths = List.of(
				"username",
				"address.street",
				"orders.products.name"
		);

		// When
		var result = dotPathQL.filter(userDetail, filterPaths);

		// Then
		assertEquals(3, result.size());
		assertEquals("john_doe", result.get( "username"));

		// Verify nested address structure
		var address = dotPathQL.mapFrom(result, "address");
		assertNotNull(address);
		assertEquals("123 Main St", address.get("street"));

		// Verify nested orders structure with products
		var orders = dotPathQL.listFrom(result, "orders");
		assertNotNull(orders);
		assertEquals(2, orders.size());

		// Debug: Print first order
		var firstOrder = orders.get(0);

		// Verify first order with its products
		var firstOrderProducts = dotPathQL.listFrom(firstOrder, "products");
		assertNotNull(firstOrderProducts, "Products list should not be null");
		assertEquals(2, firstOrderProducts.size());
		assertEquals("Laptop", firstOrderProducts.get(0).get("name"));
		assertEquals("Smartphone", firstOrderProducts.get(1).get("name"));

		// Verify second order with its product
		var secondOrder = orders.get(1);
		var secondOrderProducts = dotPathQL.listFrom(secondOrder, "products");
		assertEquals(1, secondOrderProducts.size());
		assertEquals("Headphones", secondOrderProducts.get(0).get("name"));
	}

	@Test
	void shouldReturnFilteredObjectWithArray() {
		// Given
		var userDetail = UserDetail.of();
		var filterPaths = List.of(
				"occupations.title"
		);

		// When
		var result = dotPathQL.filter(userDetail, filterPaths);

		// Then
		assertEquals(1, result.size());

		// Verify occupations structure
		var occupations = dotPathQL.listFrom(result, "occupations");
		assertEquals(2, occupations.size());
		assertEquals("Software Engineer", occupations.get(0).get("title"));
		assertEquals("Project Manager", occupations.get(1).get("title"));
	}

	@Test
	void shouldReturnFilteredObjectWithMap() {
		// Given
		var userDetail = UserDetail.of();
		var filterPaths = List.of(
				"additionalInfo.preferredLanguage",
				"additionalInfo.subscriptionStatus"
		);

		// When
		var result = dotPathQL.filter(userDetail, filterPaths);

		// Then
		assertEquals(1, result.size());

		// Verify additionalInfo structure
		var additionalInfo = dotPathQL.mapFrom(result, "additionalInfo");
		assertNotNull(additionalInfo);
		assertEquals("English", additionalInfo.get("preferredLanguage"));
		assertEquals("Active", additionalInfo.get("subscriptionStatus"));
	}

	@Test
	void shouldReturnFilteredObjectWithComplexMap() {
		// Given
		var userDetail = UserDetail.of();
		var filterPaths = List.of(
				"locations.home.street",
				"locations.work.city"
		);

		// When
		var result = dotPathQL.filter(userDetail, filterPaths);

		// Then
		assertEquals(1, result.size());

		var locations = dotPathQL.mapFrom(result, "locations");
		assertEquals(2, locations.size());

		// Verify home address structure
		var homeLocation = dotPathQL.mapFrom(locations, "home");
		assertNotNull(homeLocation);
		assertEquals("456 Elm St", homeLocation.get("street"));

		// Verify work address structure
		var workLocation = dotPathQL.mapFrom(locations, "work");
		assertNotNull(workLocation);
		assertEquals("Springfield", workLocation.get("city"));
	}

	@Test
	void shouldAddDefaultFilterPaths() {
		// Given
		var userDetail = UserDetail.of();
		var defaultPaths = List.of("username");
		var filterPaths = List.of("address.city");

		// When
		dotPathQL.addDefaultFilterPaths(defaultPaths);
		var result = dotPathQL.filter(userDetail, filterPaths);

		// Then
		assertEquals(2, result.size());
		assertEquals("john_doe", result.get("username"));
		assertEquals(1, dotPathQL.mapFrom(result, "address").size());
		assertEquals("Springfield", dotPathQL.mapFrom(result, "address").get("city"));
	}

	@Test
	void shouldReturnFilteredObjectUsingGroupedPaths() {
		// Given
		var userDetail = UserDetail.of();
		var filterPaths = List.of(
				"locations[home.street,work.city]"
		);

		// When
		var result = dotPathQL.filter(userDetail, filterPaths);

		// Then
		assertEquals(1, result.size());

		var locations = dotPathQL.mapFrom(result, "locations");
		assertEquals(2, locations.size());

		// Verify home address structure
		var homeLocation = dotPathQL.mapFrom(locations, "home");
		assertNotNull(homeLocation);
		assertEquals("456 Elm St", homeLocation.get("street"));

		// Verify work address structure
		var workLocation = dotPathQL.mapFrom(locations, "work");
		assertNotNull(workLocation);
		assertEquals("Springfield", workLocation.get("city"));
	}

	@Test
	void shouldReturnEmptyResultInvalidGroupedPaths() {
		// Given
		var userDetail = UserDetail.of();
		var filterPaths = List.of(
				"locations]home[" // Invalid grouped path
		);

		// When
		var result = dotPathQL.filter(userDetail, filterPaths);

		// Then
		assertEquals(0, result.size());
	}
}
