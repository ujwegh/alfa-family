package ru.nik.alfafamily.domain;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
	private FamilyMemberProperties properties;

	@DBRef
	private User user;

	@DBRef
	private List<Category> categories;

	public FamilyMember(String name, User user) {
		this.name = name;
		this.user = user;
	}

	@Override
	public String toString() {
		List<String> names = Collections.emptyList();
		if (categories!= null){
			names = categories.stream().map(category -> category.getName()+", ").collect(Collectors.toList());
		}

		return "FamilyMember{" +
			"id='" + id + '\'' +
			", name='" + name + '\'' +
			", properties=" + properties +
			", user=" + user.getId() +
			", categories="+ names.toString() +
			'}';
	}
}
