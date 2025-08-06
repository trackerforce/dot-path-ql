package io.github.trackerforce.fixture.record;

import java.util.Date;
import java.util.List;

public record Order(
		List<Product> products,
		Date orderDate,
		String orderId
) {
}
