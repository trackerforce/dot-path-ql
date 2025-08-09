package io.github.trackerforce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ExcludeTypeClassRecordTest {

    DotPathQL dotPathQL = new DotPathQL();

    static Stream<Arguments> userDetailProvider() {
        return Stream.of(
                Arguments.of("Record type", io.github.trackerforce.fixture.record.UserDetail.of()),
                Arguments.of("Class type", io.github.trackerforce.fixture.clazz.UserDetail.of())
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("userDetailProvider")
    void shouldExcludeSimpleNestedField(String implementation, Object userDetail) {
		// When
        var result = dotPathQL.exclude(userDetail, List.of("orders.orderId"));

		// Then
		var orders = dotPathQL.listFrom(result, "orders");
        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertFalse(orders.get(0).containsKey("orderId"));
        assertFalse(orders.get(1).containsKey("orderId"));
        assertTrue(orders.get(0).containsKey("products"));

        // roles should remain an array
        var rolesObj = result.get("roles");
        assertNotNull(rolesObj);
        assertTrue(rolesObj.getClass().isArray());
    }

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldExcludeMultipleSameBranch(String implementation, Object userDetail) {
		// When
		var result = dotPathQL.exclude(userDetail, List.of(
				"address.street",
				"address.city"
		));

		// Then
		var address = dotPathQL.mapFrom(result, "address");
		assertNotNull(address);
		assertFalse(address.containsKey("street"));
		assertFalse(address.containsKey("city"));
		assertTrue(address.containsKey("zipCode"));
		assertTrue(address.containsKey("country"));
	}

    @ParameterizedTest(name = "{0}")
    @MethodSource("userDetailProvider")
    void shouldExcludeMultipleDifferentBranches(String implementation, Object userDetail) {
		// When
        var result = dotPathQL.exclude(userDetail, List.of(
                "address.street",
                "orders.products.description",
                "additionalInfo.lastLogin"
        ));

		// Then
        var address = dotPathQL.mapFrom(result, "address");
        assertNotNull(address);
        assertFalse(address.containsKey("street"));
        assertTrue(address.containsKey("city"));

        // orders products have no description
        var orders = dotPathQL.listFrom(result, "orders");
        var firstOrderProducts = dotPathQL.listFrom(orders.get(0), "products");
        assertFalse(firstOrderProducts.get(0).containsKey("description"));

        // additionalInfo without lastLogin
        var addInfo = dotPathQL.mapFrom(result, "additionalInfo");
        assertNotNull(addInfo);
        assertFalse(addInfo.containsKey("lastLogin"));
        assertEquals("English", addInfo.get("preferredLanguage"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("userDetailProvider")
    void shouldExcludeGroupedPaths(String implementation, Object userDetail) {
		// When
        var result = dotPathQL.exclude(userDetail, List.of("locations[home.street,work.city]"));

		// Then
        var locations = dotPathQL.mapFrom(result, "locations");
        assertNotNull(locations);

        var home = dotPathQL.mapFrom(locations, "home");
        assertNotNull(home);
        assertFalse(home.containsKey("street"));
        assertTrue(home.containsKey("city"));

        var work = dotPathQL.mapFrom(locations, "work");
        assertFalse(work.containsKey("city"));
    }

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldAddDefaultExclusionPaths(String implementation, Object userDetail) {
		// When
		dotPathQL.addDefaultExcludePaths(List.of("username"));
		var result = dotPathQL.exclude(userDetail, List.of("address.city"));

		// Then
		assertFalse(result.containsKey("username"));
		var address = dotPathQL.mapFrom(result, "address");
		assertNotNull(address);
		assertFalse(address.containsKey("city"));
		assertTrue(address.containsKey("street"));
	}

	@Test
	void shouldReturnEmptyMapWhenSourceIsNull() {
		// When
		var result = dotPathQL.exclude(null, List.of("orders.orderId"));

		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
}
