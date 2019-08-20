package ru.nik.alfafamily.domain;

import java.util.Collections;
import java.util.List;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "family_members")
public class FamilyMember {

	@Id
	private String id;

	private String name;

	@DBRef
	private User user;

	public FamilyMember(String name, User user) {
		this.name = name;
		this.user = user;
	}

	@Override
	public String toString() {
		List<String> names = Collections.emptyList();

		return "FamilyMember{" +
			"id='" + id + '\'' +
			", name='" + name + '\'' +
			", user=" + user.getId() +
			'}';
	}
}
