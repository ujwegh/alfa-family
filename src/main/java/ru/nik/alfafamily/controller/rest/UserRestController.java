package ru.nik.alfafamily.controller.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.dto.UserDto;
import ru.nik.alfafamily.dto.UserRegistrationDto;
import ru.nik.alfafamily.service.UserService;

@Api(value="User rest controller", description="User manager")
@Slf4j
@RestController
@RequestMapping("/rest/users")
public class UserRestController {

	private final UserService service;

	private final Mapper mapper;

	@Autowired
	public UserRestController(UserService service, Mapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@ApiOperation(value = "Get all users", response = List.class)
	@GetMapping
	public List<UserDto> getAll() {
		log.info("Get all users");
		List<User> users = service.findAll();
		List<UserDto> dtos = new ArrayList<>();
		users.forEach(user -> dtos.add(mapper.toUserDto(user)));
		return dtos;
	}

	@ApiOperation(value = "Find user by id", response = UserDto.class)
	@GetMapping("/id/{userId}")
	public UserDto findById(@PathVariable String userId) {
		log.info("Find user by id: {}", userId);
		return mapper.toUserDto(service.findById(userId));
	}

	@ApiOperation(value = "Find user by email", response = UserDto.class)
	@GetMapping("/email/{email}")
	public UserDto findByEmail(@PathVariable String email) {
		log.info("Find user by email: {}", email);
		return mapper.toUserDto(service.findByEmail(email));
	}

	@ApiOperation(value = "Delete user by id")
	@DeleteMapping("/{userId}")
	public void delete(@PathVariable String userId) {
		log.info("Delete user by id: {}", userId);
		service.delete(userId);
	}

	@ApiOperation(value = "Create new user", response = UserDto.class)
	@PostMapping
	public UserDto create(@RequestBody UserDto dto) {
		log.info("Create new user: {}", dto.toString());

		UserRegistrationDto registrationDto = new UserRegistrationDto();
		registrationDto.setFirstName(dto.getFirstName());
		registrationDto.setLastName(dto.getLastName());
		registrationDto.setEmail(dto.getEmail());
		registrationDto.setConfirmEmail(dto.getEmail());
		registrationDto.setPassword(dto.getPassword());
		registrationDto.setConfirmPassword(dto.getPassword());
		registrationDto.setTerms(true);

		User user = service.save(registrationDto);
		return user != null ? mapper.toUserDto(user) : null;
	}

	@ApiOperation(value = "Update user", response = UserDto.class)
	public UserDto update(@RequestBody UserDto dto){
		log.info("Update user: {}", dto.toString());
		User user = service.update(dto);
		return user != null ? mapper.toUserDto(user) : null;
	}





}
