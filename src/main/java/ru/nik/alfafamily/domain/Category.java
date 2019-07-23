package ru.nik.alfafamily.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "categories")
public class Category {

	@Id
	private String id;

	@Indexed(unique=true)
	private String name;

	@DBRef
	private FamilyMember member;

	public Category(String name, FamilyMember member) {
		this.name = name;
		this.member = member;
	}
}
