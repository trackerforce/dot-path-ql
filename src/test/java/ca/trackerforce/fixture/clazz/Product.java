package ca.trackerforce.fixture.clazz;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Product {
	String id;
	String name;
	String description;
	double price;
	String category;
	int stockQuantity;

	public static Product ofLaptop() {
		return new Product(
				"1",
				"Laptop",
				"High-end gaming laptop",
				1500.00,
				"Electronics",
				5);
	}

	public static Product ofSmartphone() {
		return new Product(
				"2",
				"Smartphone",
				"Latest model smartphone",
				800.00,
				"Electronics",
				10);
	}

	public static Product ofHeadphones() {
		return new Product(
				"3",
				"Headphones",
				"Noise-cancelling headphones",
				200.00,
				"Accessories",
				15);
	}
}
