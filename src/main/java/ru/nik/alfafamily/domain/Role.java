package ru.nik.alfafamily.domain;

import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "roles")
public class Role {

	@Id
	private String id;

	private String name;

	public Role(String name) {
		this.name = name;
	}

}
