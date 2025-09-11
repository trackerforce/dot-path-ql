package ca.trackerforce.fixture.clazz;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

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
	char[] code;
	List<Integer> numbers;

	public static Address of123() {
		return new Address(
				"123 Main St",
				"Springfield",
				"IL",
				"62701",
				"USA",
				new int[]{40, 90},
				new char[]{'A', 'B', 'C'},
				List.of(1, 2, 3, 4, 5)
		);
	}

	public static Address of456() {
		return new Address(
				"456 Elm St",
				"Springfield",
				"IL",
				"62701",
				"USA",
				new int[]{39, 89},
				new char[]{'X', 'Y', 'Z'},
				List.of(6, 7, 8, 9, 10)
		);
	}

	public static Address of789() {
		return new Address(
				"789 Oak St",
				"Springfield",
				"IL",
				"62701",
				"USA",
				new int[]{39, 89},
				new char[]{'1', '2', '3'},
				List.of(11, 12, 13, 14, 15)
		);
	}
}
