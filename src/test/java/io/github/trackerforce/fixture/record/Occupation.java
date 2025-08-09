package io.github.trackerforce.fixture.record;

public record Occupation(
		String title,
		String description,
		double salary,
		String department,
		int yearsOfExperience,
		Address address
) {
}
