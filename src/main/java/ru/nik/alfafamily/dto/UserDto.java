package ru.nik.alfafamily.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UserDto {

	private String firstName;

	private String lastName;

	private String email;

	private String password;

	private String roles;

	private boolean enabled = true;

}
