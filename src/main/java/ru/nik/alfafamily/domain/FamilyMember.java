package ru.nik.alfafamily.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
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
}
