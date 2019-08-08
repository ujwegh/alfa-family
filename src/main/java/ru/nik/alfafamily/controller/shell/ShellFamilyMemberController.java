package ru.nik.alfafamily.controller.shell;

import java.util.ArrayList;
import java.util.List;
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


	@ShellMethod(value = "Get all family members")
	public String family(@ShellOption String email) {
		User user = userService.findByEmail(email);

		List<FamilyMemberDto> memberDtos = new ArrayList<>();
		List<FamilyMember> list = service.findAll(user.getId());


		list.forEach(familyMember -> memberDtos.add(mapper.toFamilyMemberDto(familyMember)));
		return String.valueOf(memberDtos);
	}

	@ShellMethod(value = "Create family familyMember")
	public String createmember(@ShellOption String email,@ShellOption String name) {
		User user = userService.findByEmail(email);
		System.out.println(user.getId());
		FamilyMemberDto dto = mapper.toFamilyMemberDto(service.create(user.getId(), name));
		return "New family member has been created: " + dto.toString();
	}

}
