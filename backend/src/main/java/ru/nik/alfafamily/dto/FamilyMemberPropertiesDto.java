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
public class FamilyMemberPropertiesDto {

	private String id;

	private String familyMemberId;

	private String color;

}
