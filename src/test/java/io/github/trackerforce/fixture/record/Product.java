package io.github.trackerforce.fixture.record;

public record Product(
		String id,
		String name,
		String description,
		double price,
		String category,
		int stockQuantity
) {
}
