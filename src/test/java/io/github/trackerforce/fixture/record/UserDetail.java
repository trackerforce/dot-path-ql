package io.github.trackerforce.fixture.record;

import java.util.List;
import java.util.Map;

public record UserDetail(
		String username,
		String email,
		String fullName,
		String phoneNumber,
		Address address,
		List<Order> orders,
		String[] roles,
		Occupation[] occupations,
		Map<String, String> additionalInfo,
		Map<String, Address> locations,
		int[] scoresArray
) {
	public static UserDetail of() {
		return new UserDetail(
				"john_doe",
				"jown@email.com",
				"John Doe",
				"+1234567890",
				Address.of123(),
				List.of(
						Order.ofOrder123(),
						Order.ofOrder456()
				),
				new String[] {"USER", "ADMIN"},
				new Occupation[] {
						Occupation.ofSoftwareEngineer(),
						Occupation.ofProjectManager()
				},
				Map.of(
						"preferredLanguage", "English",
						"subscriptionStatus", "Active",
						"lastLogin", "2023-10-01T12:00:00Z"
				),
				Map.of(
						"home", Address.of456(),
						"work", Address.of789()
				),
				new int[] {85, 90, 95}
		);
	}
}
