package ru.nik.alfafamily.domain;

import java.util.Collection;
import java.util.Date;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;



@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {

	@Id
	private String id;

	private String firstName;

	private String lastName;

	@Indexed(unique = true)
	private String email;

	private String password;

	@DBRef
	private Collection<Role> roles;

	@DBRef
	private Collection<FamilyMember> members;

	private boolean enabled = true;

	private Date lastLogin;

	public User(String firstName, String lastName, String email, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}

	public User(String firstName, String lastName, String email, String password,
		Collection<Role> roles) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}

	public String rolesToString() {
		StringBuilder s = new StringBuilder();
		roles.forEach(role -> s.append(role).append(","));
		return s.substring(s.toString().length()-2);
	}

	@Override
	public String toString() {
		return "User{" +
			"id='" + id + '\'' +
			", firstName='" + firstName + '\'' +
			", lastName='" + lastName + '\'' +
			", email='" + email + '\'' +
			", password='" + password + '\'' +
			", roles=" + roles +
			", members=" + members +
			", enabled=" + enabled +
			", lastLogin=" + lastLogin +
			'}';
	}
}
