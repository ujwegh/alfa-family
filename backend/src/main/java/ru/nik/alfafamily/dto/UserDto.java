package ru.nik.alfafamily.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
	private String id;

	private String firstName;

	private String lastName;

	private String email;

	private String password;

	private RoleDto role;

	private boolean enabled = true;
}
