package ru.nik.alfafamily.controller.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.dto.CategoryDto;
import ru.nik.alfafamily.dto.FamilyMemberDto;
import ru.nik.alfafamily.dto.FamilyMemberPropertiesDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.service.FamilyMemberService;

@Slf4j
@RestController("/rest/family")
public class FamilyMemberRestController {

	private final FamilyMemberService service;

	private final Mapper mapper;

	@Autowired
	public FamilyMemberRestController(FamilyMemberService service,
		Mapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@GetMapping("/members/{userId}")
	public List<FamilyMemberDto> getAll(@PathVariable String userId) {
		log.info("Get all family members for user: {}", userId);
		List<FamilyMember> members = service.findAll(userId);
		return members != null ? members.stream().map(mapper::toFamilyMemberDto)
			.collect(Collectors.toList()) : Collections.emptyList();
	}

	@GetMapping("/members/member/{familyMemberId}")
	public FamilyMemberDto findById(@PathVariable String familyMemberId) {
		return mapper.toFamilyMemberDto(service.findById(familyMemberId));
	}

	@DeleteMapping("/members/member/{familyMemberId}")
	public void delete(@PathVariable String familyMemberId) {
		service.delete(familyMemberId);
	}

	@PutMapping("/members/member")
	public FamilyMemberDto update(@RequestBody FamilyMemberDto dto) {
		FamilyMember member = service.update(dto.getId(), dto.getName());
		return mapper.toFamilyMemberDto(member);
	}

	@PutMapping("/members/member/categories")
	public FamilyMemberDto updateCategories(@RequestBody FamilyMemberDto dto) {
		List<CategoryDto> dtos = dto.getCategories();
		List<String> names = new ArrayList<>();
		dtos.forEach(categoryDto -> names.add(categoryDto.getName()));
		FamilyMember member = service.updateCategories(dto.getId(), names);
		return mapper.toFamilyMemberDto(member);
	}

	@PutMapping("/members/member/properties")
	public FamilyMemberDto updateProperties(@RequestBody FamilyMemberDto dto) {
		FamilyMemberPropertiesDto propertiesDto = dto.getProperties();
		if (propertiesDto != null) {
			return mapper.toFamilyMemberDto(service.updateProperties(dto.getId(), propertiesDto.getColor()));
		}
		return null;
	}

	@PostMapping("/members")
	public FamilyMemberDto create(@RequestBody FamilyMemberDto dto){
		return mapper.toFamilyMemberDto(service.create(dto.getUserId(), dto.getName()));
	}
}
