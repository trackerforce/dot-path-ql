package io.github.trackerforce.fixture.record;

import java.util.Date;
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
		Map<String, Address> locations
) {
	public static UserDetail of() {
		return new UserDetail(
				"john_doe",
				"jown@email.com",
				"John Doe",
				"+1234567890",
				new Address(
						"123 Main St",
						"Springfield",
						"IL",
						"62701",
						"USA"
				),
				List.of(
						new Order(
								List.of(
										new Product("1", "Laptop", "High-end gaming laptop", 1500.00, "Electronics", 5),
										new Product("2", "Smartphone", "Latest model smartphone", 800.00, "Electronics", 10)
								),
								new Date(),
								"order123"
						),
						new Order(
								List.of(
										new Product("3", "Headphones", "Noise-cancelling headphones", 200.00, "Accessories", 15)
								),
								new Date(),
								"order456"
						)
				),
				new String[] {"USER", "ADMIN"},
				new Occupation[] {
						new Occupation("Software Engineer", "Develops software applications", 90000.00, "Engineering", 5),
						new Occupation("Project Manager", "Manages software projects", 95000.00, "Management", 7)
				},
				Map.of(
						"preferredLanguage", "English",
						"subscriptionStatus", "Active",
						"lastLogin", "2023-10-01T12:00:00Z"
				),
				Map.of(
						"home", new Address("456 Elm St", "Springfield", "IL", "62701", "USA"),
						"work", new Address("789 Oak St", "Springfield", "IL", "62701", "USA")
				)
		);
	}
}
