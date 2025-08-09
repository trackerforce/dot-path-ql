package io.github.trackerforce.fixture.record;

public record Occupation(
		String title,
		String description,
		double salary,
		String department,
		int yearsOfExperience,
		Address address
) {
	public static Occupation ofSoftwareEngineer() {
		return new Occupation(
				"Software Engineer",
				"Develops software applications",
				90000.00,
				"Engineering",
				5,
				new Address("123 Tech St", "Tech City", "CA", "90001", "USA", new int[]{37, 122})
		);
	}

	public static Occupation ofProjectManager() {
		return new Occupation(
				"Project Manager",
				"Manages software projects",
				95000.00,
				"Management",
				7,
				new Address("456 Project Ave", "Project City", "CA", "90002", "USA", new int[]{34, 118})
		);
	}
}
