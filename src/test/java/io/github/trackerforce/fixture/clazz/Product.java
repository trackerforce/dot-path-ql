package io.github.trackerforce.fixture.clazz;

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
}
