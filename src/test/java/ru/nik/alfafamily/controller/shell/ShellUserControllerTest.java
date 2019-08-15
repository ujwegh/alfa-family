package ru.nik.alfafamily.controller.shell;


import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.annotation.DirtiesContext;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.dto.UserDto;
import ru.nik.alfafamily.dto.UserRegistrationDto;
import ru.nik.alfafamily.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@EnableMongoRepositories(basePackages = {"ru.nik.alfafamily.repository"})
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShellUserControllerTest {

	@Autowired
	private UserService service;

	@Autowired
	private Mapper mapper;

	@Autowired
	private ShellUserController controller;

	@BeforeEach
	void init() {
		controller = new ShellUserController(service, mapper);
		UserRegistrationDto registrationDto = new UserRegistrationDto();
		registrationDto.setFirstName("Firstname");
		registrationDto.setLastName("Lastname");
		registrationDto.setEmail("email");
		registrationDto.setConfirmEmail("email");
		registrationDto.setPassword("password");
		registrationDto.setConfirmPassword("password");
		registrationDto.setTerms(true);

		service.save(registrationDto);
	}


	@Test
	void register() {
		String b = controller.register("firstName12", "secondName12",
			"admin12@mail.com", "password12");
		User user = service.findAll().get(1);
		assertNotNull(b);
		assertEquals(user.toString(), b);
	}

	@Test
	void update_user() {
		User user = service.findAll().get(0);

		String roles = "USER_ROLE";
		String s = controller.update_user("Firstname2", user.getLastName(),
			"email", "password1", "password", roles);

		User user1 = service.findById(user.getId());
		UserDto dto = mapper.toUserDto(user1);
		assertNotNull(s);
		assertEquals(dto.toString(), s);
	}

	@Test
	void find_user_by_id() {
		User user = service.findAll().get(0);
		String result = controller.find_user_by_id(user.getId());
		assertEquals(user.toString(), result);
	}

	@Test
	void find_user_by_email() {
		User user = service.findAll().get(0);
		String result = controller.find_user_by_email(user.getEmail());
		assertEquals(user.toString(), result);
	}

	@Test
	void is_user_exist() {
		User user = service.findAll().get(0);
		String b = controller.is_user_exist(user.getId());
		assertEquals("true", b);
	}

	@Test
	void all_users() {
		String b = controller.all_users();
		List<User> users = service.findAll();
		assertNotNull(b);
		assertEquals(users.toString(),b);
	}
}