package ru.nik.alfafamily.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FamilyMemberProperties;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.repository.RoleRepository;
import ru.nik.alfafamily.service.FamilyMemberService;
import ru.nik.alfafamily.service.UserService;

@Component
public class Mapper {

	private final UserService userService;

	private final RoleRepository repository;

	private final FamilyMemberService familyMemberService;

	@Autowired
	public Mapper(@Lazy UserService userService,@Lazy RoleRepository repository,
		@Lazy FamilyMemberService familyMemberService) {
		this.userService = userService;
		this.repository = repository;
		this.familyMemberService = familyMemberService;
	}

//	@Autowired
//	public Mapper(FamilyMemberService fms, UserService us, RoleRepository rep) {
//		this.fms = fms;
//		this.us = us;
//		this.rep = rep;
//	}


	public FamilyMemberDto toFamilyMemberDto(FamilyMember familyMember) {
		FamilyMemberDto dto = new FamilyMemberDto();
		dto.setId(familyMember.getId());
		dto.setName(familyMember.getName());
		dto.setCategories(toCategoryDtoList(familyMember.getCategories()));
		dto.setProperties(toFamilyMemberPropertiesDto(familyMember.getProperties()));
		dto.setUserId(familyMember.getUser().getId());
		return dto;
	}

	public FamilyMember fromFamilyMemberDto(FamilyMemberDto dto) {
		FamilyMember familyMember = new FamilyMember();
		familyMember.setId(dto.getId());
		familyMember.setName(dto.getName());
		familyMember.setCategories(toCategoryList(dto.getCategories()));
		familyMember.setProperties(fromFamilyMemberPropertiesDto(dto.getProperties()));
		familyMember.setUser(userService.findById(dto.getUserId()));
		return familyMember;
	}

	public CategoryDto toCategoryDto(Category category) {
		CategoryDto dto = new CategoryDto();
		dto.setName(category.getName());
		dto.setId(category.getId());
		dto.setFamilyMemberId(category.getFamilyMember().getId());
		return dto;
	}

	public Category fromCategoryDto(CategoryDto categoryDto) {
		Category category = new Category();
		category.setId(categoryDto.getId());
		category.setName(categoryDto.getName());
		category.setFamilyMember(familyMemberService.findById(categoryDto.getFamilyMemberId()));
		return category;
	}

	public FamilyMemberPropertiesDto toFamilyMemberPropertiesDto(FamilyMemberProperties properties) {
		FamilyMemberPropertiesDto dto = new FamilyMemberPropertiesDto();
		dto.setColor(properties.getColor());
		dto.setFamilyMemberId(properties.getFamilyMember().getId());
		dto.setId(properties.getId());
		return dto;
	}

	public FamilyMemberProperties fromFamilyMemberPropertiesDto(FamilyMemberPropertiesDto dto) {
		FamilyMemberProperties properties = new FamilyMemberProperties();
		properties.setColor(dto.getColor());
		properties.setId(dto.getId());
		properties.setFamilyMember(familyMemberService.findById(dto.getFamilyMemberId()));
		return properties;
	}

	public FinancialOperationDto toFinancialOperationDto(FinancialOperation operation) {
		FinancialOperationDto dto = new FinancialOperationDto();
		dto.setId(operation.getId());
		dto.setComment(operation.getComment());
		dto.setDescription(operation.getDescription());
		dto.setCurrency(operation.getCurrency());
		dto.setSum(operation.getSum());
		dto.setAccountNumber(operation.getAccountNumber());
		dto.setType(operation.getType());
		dto.setDate(operation.getDate());
		dto.setPlanned(operation.isPlanned());
		dto.setCategory(toCategoryDto(operation.getCategory()));
		return dto;
	}


	public FinancialOperation fromFinancialOperationDto(FinancialOperationDto dto) {
		FinancialOperation operation = new FinancialOperation();
		operation.setPlanned(dto.getPlanned());
		operation.setCategory(fromCategoryDto(dto.getCategory()));
		operation.setAccountNumber(dto.getAccountNumber());
		operation.setComment(dto.getComment());
		operation.setCurrency(dto.getCurrency());
		operation.setDate(dto.getDate());
		operation.setDescription(dto.getDescription());
		operation.setId(dto.getId());
		operation.setSum(dto.getSum());
		operation.setType(dto.getType());
		return operation;
	}

	public UserDto toUserDto(User user) {
		UserDto dto = new UserDto();
		dto.setId(user.getId());
		dto.setEmail(user.getEmail());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setPassword(user.getPassword());
		dto.setRoles(user.getRoles().stream().map(this::toRoleDto).collect(Collectors.toList()));
		dto.setEnabled(user.isEnabled());
		return dto;
	}

	public User fromUserDto(UserDto dto) {
		User user = new User();
		user.setId(dto.getId());
		user.setFirstName(dto.getFirstName());
		user.setLastName(dto.getLastName());
		user.setEmail(dto.getEmail());
		user.setPassword(dto.getPassword());
		user.setEnabled(dto.isEnabled());
		user.setRoles(dto.getRoles().stream().map(this::fromRoleDto).collect(Collectors.toList()));
		return user;
	}

	public RoleDto toRoleDto(Role role) {
		RoleDto dto = new RoleDto();
		dto.setId(role.getId());
		dto.setName(role.getName());
		return dto;
	}

	public Role fromRoleDto(RoleDto dto) {
		Role role = new Role();
		role.setId(dto.getId());
		role.setName(dto.getName());
		return role;
	}

	public List<RoleDto> toRoleDtoList(List<String> names) {

		List<Role> roles = repository.findAllByNameIn(names);

		List<String> existedRoleNames = roles.stream().map(Role::getName)
			.collect(Collectors.toList());

		names.forEach(name -> {
			if (!existedRoleNames.contains(name)){
				roles.add(new Role(name));
			}
		});

		return roles.stream().map(this::toRoleDto).collect(Collectors.toList());
	}



	private List<CategoryDto> toCategoryDtoList(List<Category> categories) {
		return categories.stream().map(this::toCategoryDto)
			.collect(Collectors.toList());
	}

	private List<Category> toCategoryList(List<CategoryDto> categories) {
		return categories.stream().map(this::fromCategoryDto)
			.collect(Collectors.toList());
	}



}
