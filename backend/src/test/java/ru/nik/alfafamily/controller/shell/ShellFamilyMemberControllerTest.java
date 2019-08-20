package ru.nik.alfafamily.controller.shell;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.annotation.DirtiesContext;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.FamilyMemberDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.dto.UserRegistrationDto;
import ru.nik.alfafamily.service.FamilyMemberService;
import ru.nik.alfafamily.service.UserService;

@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@EnableMongoRepositories(basePackages = {"ru.nik.alfafamily.repository"})
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShellFamilyMemberControllerTest {

	@Autowired
	private FamilyMemberService service;
	@Autowired
	private UserService userService;
	@Autowired
	private Mapper mapper;
	@Autowired
	private ShellFamilyMemberController controller;

	@BeforeEach
	void init() {
		controller = new ShellFamilyMemberController(service, userService, mapper);

		UserRegistrationDto u = new UserRegistrationDto();
		u.setFirstName("firstName");
		u.setLastName("secondName");
		u.setEmail("admin@mail.com");
		u.setPassword("password");
		User user = userService.save(u);
		service.create(user.getId(), "Wife");
	}

	@Test
	void family_members() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = service.findAll(user.getId()).get(0);
		FamilyMemberDto familyMemberDto = mapper.toFamilyMemberDto(familyMember);
		String s = controller.family_members(familyMember.getUser().getEmail());
		assertNotNull(s);
		assertEquals("Family members for user: " + familyMember.getUser().getEmail() + "\n"
			+ Collections.singletonList(familyMemberDto).toString(), s);
	}
	@Test
	void create_member() {
		User user = userService.findAll().get(0);
		String s = controller.create_member(user.getEmail(), "Dauther");
		FamilyMemberDto dto = mapper.toFamilyMemberDto(service.findAll(user.getId()).get(1));
		assertNotNull(s);
		assertEquals("New family member has been created: " + dto.toString(), s);
	}

	@Test
	void member() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = service.findAll(user.getId()).get(0);
		FamilyMemberDto familyMemberDto = mapper.toFamilyMemberDto(familyMember);
		String s = controller.member(familyMember.getId());
		assertNotNull(s);
		assertEquals(familyMemberDto.toString(), s);
	}

	@Test
	void update_member() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = service.findAll(user.getId()).get(0);

		String s = controller.update_member(familyMember.getId(), "Son");
		familyMember.setName("Son");

		FamilyMemberDto familyMemberDto = mapper.toFamilyMemberDto(familyMember);
		assertNotNull(s);
		assertEquals(familyMemberDto.toString(), s);
	}

	@Test
	void is_member_exist() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = service.findAll(user.getId()).get(0);
		String s = controller.is_member_exist(familyMember.getId());
		assertNotNull(s);
		assertEquals("true", s);
	}


	@Test
	void delete_member() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = service.findAll(user.getId()).get(0);
		String s = controller.delete_member(familyMember.getId());
		assertNotNull(s);
		assertEquals("Family member deleted.", s);
	}
}