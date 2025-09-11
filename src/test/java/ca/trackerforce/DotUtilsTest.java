package ca.trackerforce;

import ca.trackerforce.fixture.record.UserDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DotUtilsTest {

	DotPathQL dotPathQL = new DotPathQL();

	static Stream<Arguments> userDetailProvider() {
		return Stream.of(
				Arguments.of("Record type", UserDetail.of()),
				Arguments.of("Class type", ca.trackerforce.fixture.clazz.UserDetail.of())
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldReturnSelectedArrayProperties(String implementation, Object userDetail) {
		// When
		var result = dotPathQL.toMap(userDetail);
		var roles = DotUtils.arrayFrom(result, "roles");

		// Then
		assertNotNull(roles);
		assertEquals(2, roles.length);
		assertEquals("USER", roles[0]);
		assertEquals("ADMIN", roles[1]);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldReturnSelectedListProperties(String implementation, Object userDetail) {
		// When
		var result = dotPathQL.toMap(userDetail);
		var occupations = DotUtils.listFrom(result, "occupations");

		// Then
		assertNotNull(occupations);
		assertEquals(2, occupations.size());
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldReturnSelectedMapProperties(String implementation, Object userDetail) {
		// When
		var result = dotPathQL.toMap(userDetail);
		var locations = DotUtils.mapFrom(result, "locations");

		// Then
		assertNotNull(locations);
		assertEquals(2, locations.size());
	}

	@Test
	void shouldReturnEmptyArrayWhenPropertyNotExists() {
		// When
		var unknown = DotUtils.arrayFrom(null, "unknown");

		// Then
		assertNotNull(unknown);
		assertEquals(0, unknown.length);
	}

	@Test
	void shouldReturnEmptyListWhenPropertyNotExists() {
		// When
		var unknown = DotUtils.listFrom(null, "unknown");

		// Then
		assertNotNull(unknown);
		assertEquals(0, unknown.size());
	}

	@Test
	void shouldReturnEmptyMapWhenPropertyNotExists() {
		// When
		var unknown = DotUtils.mapFrom(null, "unknown");

		// Then
		assertNotNull(unknown);
		assertEquals(0, unknown.size());
	}

	static Stream<Arguments> parsePathsArgs() {
		return Stream.of(
				Arguments.of("locations.home,locations.work", List.of(
						"locations.home",
						"locations.work"
				)),
				Arguments.of("locations,address.street,orders[product.name]", List.of(
						"locations",
						"address.street",
						"orders[product.name]"
				)),
				Arguments.of("locations[home.street,work.city]", List.of(
					"locations[home.street,work.city]"
				)),
				Arguments.of("locations[home[street,city]],locations[work[city]]", List.of(
						"locations[home[street,city]]",
						"locations[work[city]]"
				))
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("parsePathsArgs")
	void shouldParsePathsIntoList(String input, List<String> expected) {
		// When
		var paths = DotUtils.parsePaths(input);

		// Then
		assertNotNull(paths);
		assertEquals(expected.size(), paths.size());
		assertArrayEquals(expected.toArray(), paths.toArray());
	}

	@Test
	void shouldReturnEmptyListWhenParseInvalidPath() {
		// When
		var paths = DotUtils.parsePaths(null);

		// Then
		assertNotNull(paths);
		assertTrue(paths.isEmpty());
	}
}
