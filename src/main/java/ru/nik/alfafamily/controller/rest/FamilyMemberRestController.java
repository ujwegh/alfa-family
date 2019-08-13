package ru.nik.alfafamily.controller.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.dto.CategoryDto;
import ru.nik.alfafamily.dto.FamilyMemberDto;
import ru.nik.alfafamily.dto.FamilyMemberPropertiesDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.service.FamilyMemberService;

@Api(value="Family member rest controller", description="Family members manager")
@Slf4j
@RestController
@RequestMapping("/rest/{userId}/family")
public class FamilyMemberRestController {

	private final FamilyMemberService service;

	private final Mapper mapper;

	@Autowired
	public FamilyMemberRestController(FamilyMemberService service,
		Mapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Get all family members for user", response = List.class)
	@GetMapping("/members")
	public List<FamilyMemberDto> getAll(@PathVariable @Nonnull final String userId) {
		log.info("Get all family members for user: {}", userId);
		List<FamilyMember> members = service.findAll(userId);
		return members != null ? members.stream().map(mapper::toFamilyMemberDto)
			.collect(Collectors.toList()) : Collections.emptyList();
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Find family member by id", response = FamilyMemberDto.class)
	@GetMapping("/members/member/{familyMemberId}")
	public FamilyMemberDto findById(@PathVariable @Nonnull final String userId,
		@PathVariable @Nonnull final String familyMemberId) {
		log.info("Find family member by id: ", familyMemberId);
		return mapper.toFamilyMemberDto(service.findById(familyMemberId));
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Delete family member by id")
	@DeleteMapping("/members/member/{familyMemberId}")
	public void delete(@PathVariable @Nonnull final String userId,
		@PathVariable @Nonnull final String familyMemberId) {
		log.info("Delete family member by id: {}", familyMemberId);
		service.delete(familyMemberId);
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Update family member", response = FamilyMemberDto.class)
	@PutMapping("/members/member")
	public FamilyMemberDto update(@PathVariable @Nonnull final String userId,
		@RequestBody FamilyMemberDto dto) {
		log.info("Update family member: {}", dto.toString());
		FamilyMember member = service.update(dto.getId(), dto.getName());
		return mapper.toFamilyMemberDto(member);
	}

//	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
//	@ApiOperation(value = "Update categories for family member", response = FamilyMemberDto.class)
//	@PutMapping("/members/member/categories")
//	public FamilyMemberDto updateCategories(@PathVariable @Nonnull final String userId,
//		@RequestBody FamilyMemberDto dto) {
//		log.info("Update categories for family member: {}", dto.toString());
//		List<CategoryDto> dtos = dto.getCategories();
//		List<String> names = new ArrayList<>();
//		dtos.forEach(categoryDto -> names.add(categoryDto.getName()));
//		FamilyMember member = service.updateCategories(dto.getId(), names);
//		return mapper.toFamilyMemberDto(member);
//	}
//
//	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
//	@ApiOperation(value = "Update family members properties", response = FamilyMemberDto.class)
//	@PutMapping("/members/member/properties")
//	public FamilyMemberDto updateProperties(@PathVariable @Nonnull final String userId,
//		@RequestBody FamilyMemberDto dto) {
//		log.info("Update family members properties");
//		FamilyMemberPropertiesDto propertiesDto = dto.getProperties();
//		if (propertiesDto != null) {
//			return mapper.toFamilyMemberDto(service.updateProperties(dto.getId(), propertiesDto.getColor()));
//		}
//		return null;
//	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Create family member", response = FamilyMemberDto.class)
	@PostMapping("/members")
	public FamilyMemberDto create(@PathVariable @Nonnull final String userId,
		@RequestBody FamilyMemberDto dto){
		log.info("Create family member: {}", dto.toString());
		return mapper.toFamilyMemberDto(service.create(dto.getUserId(), dto.getName()));
	}
}
