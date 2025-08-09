package io.github.trackerforce.fixture.clazz;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Address {
	String street;
	String city;
	String state;
	String zipCode;
	String country;
	int[] coordinates;

	public static Address of123() {
		return new Address(
				"123 Main St",
				"Springfield",
				"IL",
				"62701",
				"USA",
				new int[]{40, 90}
		);
	}

	public static Address of456() {
		return new Address(
				"456 Elm St",
				"Springfield",
				"IL",
				"62701",
				"USA",
				new int[]{39, 89});
	}

	public static Address of789() {
		return new Address(
				"789 Oak St",
				"Springfield",
				"IL",
				"62701",
				"USA",
				new int[]{39, 89});
	}
}
