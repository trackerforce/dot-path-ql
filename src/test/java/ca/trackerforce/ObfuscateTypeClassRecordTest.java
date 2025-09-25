package ca.trackerforce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ObfuscateTypeClassRecordTest {

    DotPathQL dotPathQL = new DotPathQL();

    static Stream<Arguments> userDetailProvider() {
        return Stream.of(
                Arguments.of("Record type", ca.trackerforce.fixture.record.UserDetail.of()),
                Arguments.of("Class type", ca.trackerforce.fixture.clazz.UserDetail.of())
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("userDetailProvider")
    void shouldObfuscateSimpleNestedField(String implementation, Object userDetail) {
		// When
        var result = dotPathQL.obfuscate(userDetail, List.of("orders.orderId"));

		// Then
		var orders = DotUtils.listFrom(result, "orders");
        assertNotNull(orders);
        assertEquals(2, orders.size());
		assertEquals("****", orders.get(0).get("orderId"));
		assertEquals("****", orders.get(1).get("orderId"));
    }

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldObfuscateMultipleSameBranch(String implementation, Object userDetail) {
		// When
		var result = dotPathQL.obfuscate(userDetail, List.of(
				"address.street",
				"address.city"
		));

		// Then
		var address = DotUtils.mapFrom(result, "address");
		assertNotNull(address);
		assertEquals("****", address.get("street"));
		assertEquals("****", address.get("city"));
		assertTrue(address.containsKey("zipCode"));
		assertTrue(address.containsKey("country"));
	}

    @ParameterizedTest(name = "{0}")
    @MethodSource("userDetailProvider")
    void shouldObfuscateMultipleDifferentBranches(String implementation, Object userDetail) {
		// When
        var result = dotPathQL.obfuscate(userDetail, List.of(
                "address.street",
                "orders.products.description",
                "additionalInfo.lastLogin"
        ));

		// Then
        var address = DotUtils.mapFrom(result, "address");
        assertNotNull(address);
		assertEquals("****", address.get("street"));
		assertTrue(address.containsKey("city"));

        // orders products have no description
        var orders = DotUtils.listFrom(result, "orders");
        var firstOrderProducts = DotUtils.listFrom(orders.get(0), "products");
		assertNotNull(firstOrderProducts);
		assertEquals(2, firstOrderProducts.size());
		assertEquals("****", firstOrderProducts.get(0).get("description"));
		assertEquals("****", firstOrderProducts.get(1).get("description"));

        // additionalInfo without lastLogin
        var addInfo = DotUtils.mapFrom(result, "additionalInfo");
        assertNotNull(addInfo);
		assertEquals("****", addInfo.get("lastLogin"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("userDetailProvider")
    void shouldObfuscateGroupedPaths(String implementation, Object userDetail) {
		// When
        var result = dotPathQL.obfuscate(userDetail, List.of("locations[home.street,work.city]"));

		// Then
        var locations = DotUtils.mapFrom(result, "locations");
        assertNotNull(locations);

        var home = DotUtils.mapFrom(locations, "home");
        assertNotNull(home);
		assertEquals("****", home.get("street"));
		assertTrue(home.containsKey("city"));

        var work = DotUtils.mapFrom(locations, "work");
		assertNotNull(work);
		assertEquals("****", work.get("city"));
		assertTrue(work.containsKey("street"));
    }

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldAddDefaultObfuscatePaths(String implementation, Object userDetail) {
		// When
		dotPathQL.addDefaultObfuscatePaths(List.of("username"));
		var result = dotPathQL.obfuscate(userDetail, List.of("address.city"));

		// Then
		assertEquals("****", result.get("username"));
		var address = DotUtils.mapFrom(result, "address");
		assertNotNull(address);
		assertEquals("****", address.get("city"));
		assertTrue(address.containsKey("street"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldObfuscateSimpleNestedFieldFromMapSource(String implementation, Object userDetail) {
		// When
		var source = dotPathQL.toMap(userDetail); // Convert to Map for testing
		var result = dotPathQL.obfuscate(source, List.of("orders.orderId"));

		// Then
		var orders = DotUtils.listFrom(result, "orders");
		assertNotNull(orders);
		assertEquals(2, orders.size());
		assertEquals("****", orders.get(0).get("orderId"));
		assertEquals("****", orders.get(1).get("orderId"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldNotObfuscateWhenPathNotExists(String implementation, Object userDetail) {
		// When
		var source = dotPathQL.toMap(userDetail); // Convert to Map for testing
		var result = dotPathQL.obfuscate(source, List.of(
				"invalidProperty" // Non-existent property
		));

		// Then
		assertNotNull(result);
		assertEquals(source, result); // Should be unchanged
	}

	@Test
	void shouldReturnEmptyMapWhenSourceIsNull() {
		// When
		var result = dotPathQL.obfuscate(null, List.of("orders.orderId"));

		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

}
