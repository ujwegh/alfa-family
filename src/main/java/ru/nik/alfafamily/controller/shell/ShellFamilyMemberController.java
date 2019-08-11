package ru.nik.alfafamily.controller.shell;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.FamilyMemberDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.service.FamilyMemberService;
import ru.nik.alfafamily.service.UserService;

@Slf4j
@ShellComponent
public class ShellFamilyMemberController {

	private final FamilyMemberService service;

	private final UserService userService;

	private final Mapper mapper;

	@Autowired
	public ShellFamilyMemberController(FamilyMemberService service,
		UserService userService, Mapper mapper) {
		this.service = service;
		this.userService = userService;
		this.mapper = mapper;
	}


	@ShellMethod("Get all family members")
	public String familymembers(@ShellOption String email) {
		User user = userService.findByEmail(email);
		if (user == null) return "User with email: <"+ email+"> doesn't exist.";
		List<FamilyMember> list = service.findAll(user.getId());
		List<FamilyMemberDto> memberDtos = list.stream()
			.map(mapper::toFamilyMemberDto)
			.collect(Collectors.toList());
		return "Family members for user: " + email + "\n" + memberDtos.toString();
	}

	@ShellMethod("Create family familyMember")
	public String createmember(@ShellOption String email, @ShellOption String name) {
		User user = userService.findByEmail(email);
		FamilyMemberDto dto = mapper.toFamilyMemberDto(service.create(user.getId(), name));
		return "New family member has been created: " + dto.toString();
	}


	@ShellMethod("Get family member by id")
	public String member(@ShellOption String familyMemberId) {
		FamilyMember member = service.findById(familyMemberId);
		if (member == null) {
			return "Family member with id: <" + familyMemberId + "> doesn't exist.";
		}
		FamilyMemberDto dto = mapper.toFamilyMemberDto(member);
		return dto.toString();
	}

	@ShellMethod("Update family member")
	public String updatemember(@ShellOption String familyMemberId, @ShellOption String name) {
		FamilyMember member = service.update(familyMemberId, name);
		if (member == null) {
			return "Update family member was failed.";
		}
		FamilyMemberDto dto = mapper.toFamilyMemberDto(member);
		return dto.toString();
	}

	@ShellMethod("Check is family member exists")
	public String ismemberexist(@ShellOption String familyMemberId) {
		return service.isFamilyMemberExists(familyMemberId).toString();
	}

	@ShellMethod("Update family member categories")
	public String updatemembercategories(@ShellOption String familyMemberId,
		@ShellOption String categories) {
		List<String> categoryList;
		if (categories.contains(",")) {
			categoryList = Arrays.asList(categories.split(","));
		} else {
			categoryList = Collections.singletonList(categories);
		}

		FamilyMember member = service.updateCategories(familyMemberId, categoryList);
		FamilyMemberDto dto = mapper.toFamilyMemberDto(member);
		return dto.toString();
	}

	@ShellMethod("Update family member properties")
	public String updatememberproperties(@ShellOption String familyMemberId,
		@ShellOption String color) {
		FamilyMember member = service.updateProperties(familyMemberId, color);
		if (member == null) {
			return "Update family member was failed.";
		}
		FamilyMemberDto dto = mapper.toFamilyMemberDto(member);
		return dto.toString();
	}

	@ShellMethod("Delete family member")
	public String deletemember(@ShellOption String familyMemberId) {
		boolean b = service.delete(familyMemberId);
		return b ? "Family member deleted." : "Delete family member failed.";
	}

}
