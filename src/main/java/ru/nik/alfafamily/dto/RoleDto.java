package ru.nik.alfafamily.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class RoleDto {

	private String id;

	private String name;

	@Override
	public String toString() {
		return "RoleDto{" +
			"id='" + id + '\'' +
			", name='" + name + '\'' +
			'}';
	}
}
