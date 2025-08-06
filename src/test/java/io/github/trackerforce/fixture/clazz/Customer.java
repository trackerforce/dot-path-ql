package io.github.trackerforce.fixture.clazz;

import lombok.Data;

import java.util.List;

@Data
public class Customer {
	private String name;
	private String email;
	private List<Feature> features;
	private Metadata metadata;

	public static Customer of() {
		Customer customer = new Customer();
		customer.setName("Default Name");
		customer.setEmail("customer@email.com");

		Feature feature1 = new Feature();
		feature1.setName("Default Feature");
		feature1.setDescription("This is a default feature description.");
		feature1.setEnabled(true);
		feature1.setMetadata(List.of(
				new Metadata(new String[]{"defaultTag"}, "defaultPassword"),
				new Metadata(new String[]{"anotherTag"}, "anotherPassword")));

		Feature feature2 = new Feature();
		feature2.setName("Additional Feature");
		feature2.setDescription("This is an additional feature description.");
		feature2.setEnabled(false);

		customer.setFeatures(List.of(feature1, feature2));

		String[] tags = {"tag1", "tag2"};
		Metadata metadata = new Metadata(tags, "securePassword");
		customer.setMetadata(metadata);
		return customer;
	}
}
