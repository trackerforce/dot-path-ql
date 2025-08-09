package io.github.trackerforce.fixture.clazz;

import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Order {
	List<Product> products;
	Date orderDate;
	String orderId;

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
