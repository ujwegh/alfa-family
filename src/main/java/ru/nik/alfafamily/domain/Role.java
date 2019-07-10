package ru.nik.alfafamily.domain;

import java.util.List;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@ToString
@Document(collection = "roles")
public class Role {

	@Id
	private String id;

	private String name;

	@DBRef
	private List<User> users;

	public Role(String name) {
		this.name = name;
	}

}
