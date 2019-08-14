package ru.nik.alfafamily.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
	private String id;

	private String firstName;

	private String lastName;

	private String email;

	private String password;

	private List<RoleDto> roles;

	private boolean enabled = true;

	@Override
	public String toString() {
		return "UserDto{" +
			"id='" + id + '\'' +
			", firstName='" + firstName + '\'' +
			", lastName='" + lastName + '\'' +
			", email='" + email + '\'' +
			", password='" + password + '\'' +
			", roles=" + (roles != null ? roles.toString() : "[]") +
			", enabled=" + enabled +
			'}';
	}
}
