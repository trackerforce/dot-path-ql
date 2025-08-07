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
}
