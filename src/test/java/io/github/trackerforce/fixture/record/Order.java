package io.github.trackerforce.fixture.record;

import java.util.Date;
import java.util.List;

public record Order(
		List<Product> products,
		Date orderDate,
		String orderId
) {
	public static Order ofOrder123() {
		return new Order(
				List.of(Product.ofLaptop(), Product.ofSmartphone()),
				new Date(),
				"order123"
		);
	}

	public static Order ofOrder456() {
		return new Order(
				List.of(Product.ofHeadphones()),
				new Date(),
				"order456"
		);
	}
}
