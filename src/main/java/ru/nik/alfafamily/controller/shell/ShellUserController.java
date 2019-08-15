package ru.nik.alfafamily.controller.shell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.dto.RoleDto;
import ru.nik.alfafamily.dto.UserDto;
import ru.nik.alfafamily.dto.UserRegistrationDto;
import ru.nik.alfafamily.service.UserService;

@Slf4j
@ShellComponent
public class ShellUserController {

	private final UserService service;

	private final Mapper mapper;

	@Autowired
	public ShellUserController(UserService service, Mapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@ShellMethod("Register new user")
	public String register(@ShellOption String firstName, @ShellOption String lastName,
		@ShellOption String password, @ShellOption String email) {
		log.info("Creating user..");
		UserRegistrationDto dto = new UserRegistrationDto();

		dto.setFirstName(firstName);
		dto.setLastName(lastName);
		dto.setPassword(password);
		dto.setConfirmPassword(password);
		dto.setEmail(email);
		dto.setConfirmEmail(email);
		dto.setTerms(true);
		return service.save(dto).toString();
	}

	@ShellMethod("Update existed user")
	public String update_user(@ShellOption String firstName, @ShellOption String lastName,
		@ShellOption String email, @ShellOption String password, @ShellOption String enabled,
		@ShellOption String role) {
		log.info("Updating user..");
		User user = service.findByEmail(email);

		UserDto dto = new UserDto(user.getId(), firstName, lastName, email, password,
			new RoleDto(null ,role), Boolean.valueOf(enabled));
		return mapper.toUserDto(service.update(dto)).toString();
	}

	@ShellMethod("Fine user by Id")
	public String find_user_by_id(@ShellOption String userId) {
		log.info("Find user by id..");
		service.isUserExistsById(userId);
		return String.valueOf(service.findById(userId));
	}

	@ShellMethod("Find user by email")
	public String find_user_by_email(@ShellOption String email) {
		log.info("Find user by email..");
		service.isUserExistsByEmail(email);
		return String.valueOf(service.findByEmail(email));
	}

	@ShellMethod("check user exists")
	public String is_user_exist(@ShellOption String userId) {
		log.info("Find user by id..");
		return service.isUserExistsById(userId).toString();
	}

	@ShellMethod("Find all users")
	public String all_users() {
		log.info("Find all users..");
		List<User> users = service.findAll();
		List<UserDto> allUsers = new ArrayList<>();
		return users.toString();
	}

}
