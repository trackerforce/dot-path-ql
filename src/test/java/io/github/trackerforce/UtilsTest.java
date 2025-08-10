package io.github.trackerforce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UtilsTest {

	DotPathQL dotPathQL = new DotPathQL();

	static Stream<Arguments> userDetailProvider() {
		return Stream.of(
				Arguments.of("Record type", io.github.trackerforce.fixture.record.UserDetail.of()),
				Arguments.of("Class type", io.github.trackerforce.fixture.clazz.UserDetail.of())
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldReturnSelectedArrayProperties(String implementation, Object userDetail) {
		// When
		var result = dotPathQL.toMap(userDetail);
		var roles = dotPathQL.arrayFrom(result, "roles");

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
		var occupations = dotPathQL.listFrom(result, "occupations");

		// Then
		assertNotNull(occupations);
		assertEquals(2, occupations.size());
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldReturnSelectedMapProperties(String implementation, Object userDetail) {
		// When
		var result = dotPathQL.toMap(userDetail);
		var locations = dotPathQL.mapFrom(result, "locations");

		// Then
		assertNotNull(locations);
		assertEquals(2, locations.size());
	}

	@Test
	void shouldReturnEmptyArrayWhenPropertyNotExists() {
		// When
		var unknown = dotPathQL.arrayFrom(null, "unknown");

		// Then
		assertNotNull(unknown);
		assertEquals(0, unknown.length);
	}

	@Test
	void shouldReturnEmptyListWhenPropertyNotExists() {
		// When
		var unknown = dotPathQL.listFrom(null, "unknown");

		// Then
		assertNotNull(unknown);
		assertEquals(0, unknown.size());
	}

	@Test
	void shouldReturnEmptyMapWhenPropertyNotExists() {
		// When
		var unknown = dotPathQL.mapFrom(null, "unknown");

		// Then
		assertNotNull(unknown);
		assertEquals(0, unknown.size());
	}
}
