package ca.trackerforce;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PipelineTypeClassRecordTest {

    DotPathQL dotPathQL = new DotPathQL();

    static Stream<Arguments> userDetailProvider() {
        return Stream.of(
                Arguments.of("Record type", ca.trackerforce.fixture.record.UserDetail.of()),
                Arguments.of("Class type", ca.trackerforce.fixture.clazz.UserDetail.of())
        );
    }

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldPipeObfuscateFields(String implementation, Object userDetail) {
		// Given
		var result = dotPathQL.pipeline(userDetail)
				.obfuscate(List.of("address.zipCode"))
				.execute();

		// Then
		var address = DotUtils.mapFrom(result, "address");
		assertNotNull(address);
		assertEquals("****", address.get("zipCode"));
		assertTrue(address.containsKey("street"));
		assertTrue(address.containsKey("city"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldPipeExcludeFields(String implementation, Object userDetail) {
		// Given
		var result = dotPathQL.pipeline(userDetail)
				.exclude(List.of("additionalInfo.lastLogin"))
				.execute();

		// Then
		var addInfo = DotUtils.mapFrom(result, "additionalInfo");
		assertNotNull(addInfo);
		assertFalse(addInfo.containsKey("lastLogin")); // Excluded
		assertTrue(addInfo.containsKey("subscriptionStatus"));
		assertTrue(addInfo.containsKey("preferredLanguage"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldPipeObfuscateAndExcludeFields(String implementation, Object userDetail) {
		// Given
		var result = dotPathQL.pipeline(userDetail)
				.exclude(List.of("additionalInfo.lastLogin"))
				.obfuscate(List.of("address.zipCode"))
				.execute();

		// Then
		var address = DotUtils.mapFrom(result, "address");
		assertNotNull(address);
		assertEquals("****", address.get("zipCode"));
		assertTrue(address.containsKey("street"));
		assertTrue(address.containsKey("city"));

		var addInfo = DotUtils.mapFrom(result, "additionalInfo");
		assertNotNull(addInfo);
		assertFalse(addInfo.containsKey("lastLogin")); // Excluded
		assertTrue(addInfo.containsKey("subscriptionStatus"));
		assertTrue(addInfo.containsKey("preferredLanguage"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("userDetailProvider")
	void shouldPipeObfuscateAndExcludeFieldsUsingDefaultPaths(String implementation, Object userDetail) {
		// Given
		dotPathQL.addDefaultExcludePaths(List.of("additionalInfo.lastLogin"));
		dotPathQL.addDefaultObfuscatePaths(List.of("address.zipCode"));

		var result = dotPathQL.pipeline(userDetail)
				.exclude()
				.obfuscate()
				.execute();

		// Then
		var address = DotUtils.mapFrom(result, "address");
		assertNotNull(address);
		assertEquals("****", address.get("zipCode"));
		assertTrue(address.containsKey("street"));
		assertTrue(address.containsKey("city"));

		var addInfo = DotUtils.mapFrom(result, "additionalInfo");
		assertNotNull(addInfo);
		assertFalse(addInfo.containsKey("lastLogin")); // Excluded
		assertTrue(addInfo.containsKey("subscriptionStatus"));
		assertTrue(addInfo.containsKey("preferredLanguage"));
	}
}
