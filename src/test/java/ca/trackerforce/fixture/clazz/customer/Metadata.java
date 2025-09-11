package ca.trackerforce.fixture.clazz.customer;

import lombok.Setter;

@Setter
public class Metadata {

	private String[] tags;
	private String password;

	public Metadata(String[] tags, String password) {
		this.tags = tags;
		this.password = password;
	}
}
