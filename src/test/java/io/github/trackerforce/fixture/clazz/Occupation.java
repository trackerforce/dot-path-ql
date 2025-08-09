package io.github.trackerforce.fixture.clazz;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Occupation {
	String title;
	String description;
	double salary;
	String department;
	int yearsOfExperience;
	Address address;
}
