package ru.nik.alfafamily.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FamilyMemberProperties;
import ru.nik.alfafamily.domain.User;

class FamilyMemberMapperTest {

	@Test
	void toFamilyMemberDto() {
		FamilyMember familyMember = new FamilyMember("test",
			new User("firstname", "secondname", "email", "password"));
		familyMember.setId("a");
		FamilyMemberProperties properties = new FamilyMemberProperties(familyMember, "red");

		familyMember
			.setCategories(Collections.singletonList(new Category("продукты", familyMember)));
		familyMember.setProperties(properties);
		FamilyMemberDto dto = FamilyMemberMapper.INSTANCE.toFamilyMemberDto(familyMember);

		assertNotNull(dto);
		assertEquals(familyMember.getName(), dto.getName());
		assertEquals(familyMember.getUser().getId(), dto.getUserId());
		assertEquals(familyMember.getCategories().size(), dto.getCategories().size());
		assertEquals(familyMember.getCategories().get(0).getName(),
			dto.getCategories().get(0).getName());
		assertEquals(familyMember.getCategories().get(0).getFamilyMember().getId(),
			dto.getCategories().get(0).getFamilyMemberId());
		assertEquals(familyMember.getProperties().getColor(), dto.getProperties().getColor());
	}


	@Test
	void fromFamilyMemberDto() {

		FamilyMemberDto familyMemberDto = new FamilyMemberDto("idd","test", "user_id",
			Collections.singletonList(new CategoryDto("c","shops", "idd")),
			new FamilyMemberPropertiesDto("d","idd", "red"));

		FamilyMember familyMember = FamilyMemberMapper.INSTANCE.fromFamilyMemberDto(familyMemberDto);

		assertNotNull(familyMember);
		assertEquals(familyMemberDto.getName(), familyMember.getName());
		assertEquals(familyMemberDto.getId(), familyMember.getId());
		assertEquals(familyMemberDto.getCategories().size(), familyMember.getCategories().size());
		assertEquals(familyMemberDto.getCategories().get(0).getName(), familyMember.getCategories().get(0).getName());
		assertEquals(familyMemberDto.getCategories().get(0).getFamilyMemberId(), familyMember.getCategories().get(0).getFamilyMember());
		assertEquals(familyMemberDto.getCategories().get(0).getFamilyMemberId(), familyMember.getCategories().get(0).getFamilyMember().getId());
		assertEquals(familyMemberDto.getProperties().getColor(), familyMember.getProperties().getColor());
		assertEquals(familyMemberDto.getProperties().getFamilyMemberId(), familyMember.getProperties().getFamilyMember().getId());



	}
}