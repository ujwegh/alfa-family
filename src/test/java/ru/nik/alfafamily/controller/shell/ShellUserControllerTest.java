package ru.nik.alfafamily.controller.shell;


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

	}

	@Test
	void updateuser() {

	}

	@Test
	void finduserbyid() {
		User user = service.findAll().get(0);
		String result = controller.finduserbyid(user.getId());
		assertEquals(user.toString(), result);
	}

	@Test
	void finduserbyemail() {

	}

	@Test
	void isuserexist() {

	}

	@Test
	void allusers() {

	}
}