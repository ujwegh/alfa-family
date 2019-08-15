package ru.nik.alfafamily.dto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.nik.alfafamily.domain.Budget;
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

	public BudgetDto toBudgetDto(Budget budget){
		BudgetDto dto = new BudgetDto();
		dto.setUserId(budget.getUserId());
		dto.setFamilyMemberId(budget.getFamilyMemberId());
		dto.setIncome(budget.getIncome());
		dto.setOutcome(budget.getOutcome());
		dto.setStartDate(budget.getStartDate());
		dto.setEndDate(budget.getEndDate());
		return dto;
	}

	public FamilyMemberDto toFamilyMemberDto(FamilyMember familyMember) {
		FamilyMemberDto dto = new FamilyMemberDto();
		dto.setId(familyMember.getId());
		dto.setName(familyMember.getName());
		dto.setUserId(familyMember.getUser().getId());
		return dto;
	}

	public FamilyMember fromFamilyMemberDto(FamilyMemberDto dto) {
		FamilyMember familyMember = new FamilyMember();
		familyMember.setId(dto.getId());
		familyMember.setName(dto.getName());
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
		if (properties == null) return null;
		FamilyMemberPropertiesDto dto = new FamilyMemberPropertiesDto();
		dto.setColor(properties.getColor());
		dto.setFamilyMemberId(properties.getFamilyMember().getId());
		dto.setId(properties.getId());
		return dto;
	}

	public FamilyMemberProperties fromFamilyMemberPropertiesDto(FamilyMemberPropertiesDto dto) {
		if (dto == null) return null;
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
		dto.setRole(toRoleDto(user.getRole()));
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
		user.setRole(fromRoleDto(dto.getRole()));
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

//	public List<RoleDto> toRoleDtoList(List<String> names) {
//
//		List<Role> roles = repository.findAllByNameIn(names);
//
//		List<String> existedRoleNames = roles.stream().map(Role::getName)
//			.collect(Collectors.toList());
//
//		names.forEach(name -> {
//			if (!existedRoleNames.contains(name)){
//				roles.add(new Role(name));
//			}
//		});
//
//		return roles.stream().map(this::toRoleDto).collect(Collectors.toList());
//	}


//
//	private List<CategoryDto> toCategoryDtoList(List<Category> categories) {
//		if (categories == null) return Collections.emptyList();
//		return categories.stream().map(this::toCategoryDto)
//			.collect(Collectors.toList());
//	}
//
//	private List<Category> toCategoryList(List<CategoryDto> categories) {
//		if (categories == null) return Collections.emptyList();
//		return categories.stream().map(this::fromCategoryDto)
//			.collect(Collectors.toList());
//	}



}
