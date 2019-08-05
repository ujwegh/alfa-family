package ru.nik.alfafamily.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FamilyMemberProperties;
import ru.nik.alfafamily.service.CategoryService;
import ru.nik.alfafamily.service.FamilyMemberPropertiesService;
import ru.nik.alfafamily.service.UserService;

@Mapper(componentModel = "spring", uses = {CategoryService.class, FamilyMemberPropertiesService.class,
	UserService.class})
public interface FamilyMemberMapper {

	FamilyMemberMapper INSTANCE = Mappers.getMapper(FamilyMemberMapper.class);

//	@Mapping(target = "userId", source = "user.id")
	FamilyMemberDto toFamilyMemberDto(FamilyMember familyMember);

//	@Mapping(target = "user", source = "userId")
	FamilyMember fromFamilyMemberDto(FamilyMemberDto familyMemberDto);

//	@Mapping(target = "familyMemberId", source = "familyMember.id")
	CategoryDto toCategoryDto(Category category);

//	@Mapping(target = "familyMember", source = "familyMemberId")
	Category fromCategoryDto(CategoryDto categoryDto);

//	@Mapping(target = "familyMemberId", source = "familyMember.id")
	FamilyMemberPropertiesDto propertiesToFamilyMemberPropertiesDto(FamilyMemberProperties properties);

//	@Mapping(target = "familyMember", source = "familyMemberId")
	FamilyMemberProperties fromFamilyMemberPropertiesDto(FamilyMemberPropertiesDto propertiesDto);


}
