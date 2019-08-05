package ru.nik.alfafamily.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.nik.alfafamily.domain.FamilyMember;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberDto {

	private String id;

	private String name;

	private String userId;

	private List<CategoryDto> categories;

	private FamilyMemberPropertiesDto properties;

//	FamilyMemberDto toFamilyMemberDto(FamilyMember familyMember) {
//
//
//
//
//
//
//	}


}
