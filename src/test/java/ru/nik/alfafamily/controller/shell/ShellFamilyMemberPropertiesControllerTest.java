package ru.nik.alfafamily.controller.shell;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
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
import ru.nik.alfafamily.domain.FamilyMemberProperties;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.FamilyMemberPropertiesDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.dto.UserRegistrationDto;
import ru.nik.alfafamily.service.FamilyMemberPropertiesService;
import ru.nik.alfafamily.service.FamilyMemberService;
import ru.nik.alfafamily.service.UserService;

@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@EnableMongoRepositories(basePackages = {"ru.nik.alfafamily.repository"})
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShellFamilyMemberPropertiesControllerTest {

	@Autowired
	private UserService userService;

	@Autowired
	private FamilyMemberPropertiesService service;

	@Autowired
	private Mapper mapper;

	@Autowired
	private ShellFamilyMemberPropertiesController controller;

	@Autowired
	private FamilyMemberService familyMemberService;

	@BeforeEach
	void init() {
		controller = new ShellFamilyMemberPropertiesController(service, mapper);
		UserRegistrationDto u = new UserRegistrationDto();
		u.setFirstName("firstName");
		u.setLastName("secondName");
		u.setEmail("admin@mail.com");
		u.setPassword("password");
		User user = userService.save(u);

		FamilyMember member = familyMemberService.create(user.getId(), "Wife");

		Map<String, String> properties = new HashMap<>();
		properties.put("color", "red");
		service.createOrUpdate(member.getId(), properties);
	}

	@Test
	void member_properties() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = familyMemberService.findAll(user.getId()).get(0);

		FamilyMemberProperties properties = service.findByFamilyMemberId(familyMember.getId());
		String s = controller.member_properties(properties.getId());

		FamilyMemberProperties properties1 = service.findById(properties.getId());
		FamilyMemberPropertiesDto dto = mapper.toFamilyMemberPropertiesDto(properties1);
		assertNotNull(s);
		assertEquals(dto.toString(), s);
	}

	@Test
	void create_member_props() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = familyMemberService.findAll(user.getId()).get(0);

		String s = controller.create_member_props(familyMember.getId(), "yellow");
		FamilyMemberProperties properties = service.findByFamilyMemberId(familyMember.getId());
		FamilyMemberPropertiesDto dto = mapper.toFamilyMemberPropertiesDto(properties);
		assertNotNull(s);
		assertEquals(dto.toString(), s);
	}

	@Test
	void delete_props() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = familyMemberService.findAll(user.getId()).get(0);
		String s = controller.delete_property(familyMember.getId());
		boolean b = service.delete(familyMember.getId());
		assertNotNull(b);
		assertEquals("Family member properties deleted.", s);
	}
}