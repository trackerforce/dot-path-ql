package ca.trackerforce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PrintTypeClassRecordTest {

	DotPathQL dotPathQL = new DotPathQL();

	static Stream<Arguments> userDetailProvider() {
		return Stream.of(
				Arguments.of("Record type", ca.trackerforce.fixture.record.UserDetail.of()),
				Arguments.of("Class type", ca.trackerforce.fixture.clazz.UserDetail.of())
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldPrintTypeClassRecord(String implementation, Object userDetail) {
		// Given
		var result = dotPathQL.toMap(userDetail);

		// When
		var json = dotPathQL.toJson(result, false);

		// Then
		assertNotNull(json);
		assertEquals(1, json.indexOf("\"username\": \"john_doe\""));
		assertEquals(118, json.indexOf("\"street\": \"123 Main St\""));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldPrintPrettyTypeClassRecord(String implementation, Object userDetail) {
		// Given
		var result = dotPathQL.toMap(userDetail);

		// When
		var json = dotPathQL.toJson(result, true);

		// Then
		assertNotNull(json);
		assertEquals(2, json.indexOf("  \"username\": \"john_doe\""));
		assertEquals(130, json.indexOf("    \"street\": \"123 Main St\""));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldPrintPrettyIndentTypeClassRecord(String implementation, Object userDetail) {
		// Given
		var result = dotPathQL.toMap(userDetail);

		// When
		var json = dotPathQL.toJson(result, 4);

		// Then
		assertNotNull(json);
		assertEquals(2, json.indexOf("    \"username\": \"john_doe\""));
		assertEquals(140, json.indexOf("        \"street\": \"123 Main St\""));
	}

	@Test
	void shouldPrintEmptyJsonForNullInput() {
		// When
		var json = dotPathQL.toJson(new HashMap<>(), false);

		// Then
		assertNotNull(json);
		assertEquals("{}", json);
	}

	@Test
	void shouldPrintNullForNullResult() {
		// When
		var json = dotPathQL.toJson(null, false);

		// Then
		assertNotNull(json);
		assertEquals("null", json);
	}
}
