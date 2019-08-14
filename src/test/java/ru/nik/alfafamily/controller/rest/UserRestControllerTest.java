package ru.nik.alfafamily.controller.rest;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.dto.RoleDto;
import ru.nik.alfafamily.dto.UserDto;
import ru.nik.alfafamily.dto.UserRegistrationDto;
import ru.nik.alfafamily.security.AuthenticationSuccessHandlerImpl;
import ru.nik.alfafamily.service.UserService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserRestController.class)
class UserRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService service;

	@MockBean
	private Mapper mapper;

	@MockBean
	private AuthenticationSuccessHandlerImpl successHandler;

	private List<User> users = new ArrayList<>();

	private List<UserDto> userDtos = new ArrayList<>();

	@BeforeEach
	void init() {
		User first = new User();
		first.setId("first111");
		first.setFirstName("Firstname");
		first.setLastName("Lastname");
		first.setEmail("email");
		first.setPassword("password");
		first.setEnabled(true);
		first.setRoles(Collections.singleton(new Role("ROLE_USER")));
		User second = new User("Firstname", "Lasname", "Email2", "passwrd",
			Collections.singletonList(new Role("ROLE_ADMIN")));
		second.setId("second222");

		users.add(first);
		users.add(second);

		userDtos.add(toUserDto(first));
		userDtos.add(toUserDto(second));
	}


	@WithMockUser(authorities = "ROLE_ADMIN")
	@Test
	void getAll() throws Exception {
		Mockito.when(service.findAll()).thenReturn(users);
		Mockito.when(mapper.toUserDto(users.get(0))).thenReturn(userDtos.get(0));
		Mockito.when(mapper.toUserDto(users.get(1))).thenReturn(userDtos.get(1));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/users")
			.accept(MediaType.APPLICATION_JSON_VALUE);
		mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(listAsJsonString(userDtos))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).findAll();
	}

	@WithMockUser(authorities = "ROLE_ADMIN")
	@Test
	void findById() throws Exception {
		Mockito.when(service.findById("first111")).thenReturn(users.get(0));
		Mockito.when(mapper.toUserDto(users.get(0))).thenReturn(userDtos.get(0));
		UserDto dto = userDtos.get(0);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/users/id/{userId}", "first111")
			.accept(MediaType.APPLICATION_JSON_VALUE);
		mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(dto))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).findById("first111");
	}

	@WithMockUser(authorities = "ROLE_ADMIN")
	@Test
	void findByEmail() throws Exception {
		Mockito.when(service.findByEmail("email")).thenReturn(users.get(0));
		Mockito.when(mapper.toUserDto(users.get(0))).thenReturn(userDtos.get(0));
		UserDto dto = userDtos.get(0);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/users/email/{email}", "email")
			.accept(MediaType.APPLICATION_JSON_VALUE);
		mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(dto))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).findByEmail("email");
	}

	@WithMockUser(authorities = "ROLE_ADMIN")
	@Test
	void delete() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/users/{userId}", "first111")
			.with(csrf());
		this.mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		verify(this.service, Mockito.atLeastOnce()).delete("first111");
	}

	@WithMockUser(authorities = "ROLE_ADMIN")
	@Test
	void create() throws Exception {
		UserRegistrationDto registrationDto = new UserRegistrationDto();
		registrationDto.setFirstName("qwe");
		registrationDto.setLastName("qwe");
		registrationDto.setEmail("qwe@aa.ru");
		registrationDto.setConfirmEmail("qwe@aa.ru");
		registrationDto.setPassword("passqweword");
		registrationDto.setConfirmPassword("passqweword");
		registrationDto.setTerms(true);

		User newUser = new User("qwe", "qwe", "qwe@aa.ru", "passqweword",
			Collections.singletonList(new Role("ROLE_USER")));
		newUser.setId("third333");

		Mockito.when(service.save(registrationDto)).thenReturn(newUser);
		Mockito.when(mapper.toUserDto(newUser)).thenReturn(toUserDto(newUser));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/rest/users")
			.with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content("{\"id\":null,\"firstName\":\"qwe\",\"lastName\":\"qwe\",\"email\":\"qwe@aa.ru\","
				+ "\"password\":\"passqweword\",\"roles\":[{\"id\":null,\"name\":null}],\"enabled\":true}");
		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json("{\"id\":\"third333\",\"firstName\":\"qwe\",\"lastName\":\"qwe\",\"email\":\"qwe@aa.ru\",\"password\":\"passqweword\",\"roles\":\"null\",\"enabled\":true}")).andReturn();
		verify(this.service, Mockito.atLeastOnce()).save(registrationDto);
	}

	@WithMockUser(authorities = "ROLE_ADMIN")
	@Test
	void update() throws Exception {
		User user = users.get(0);
		user.setPassword("newPass");
		UserDto userDto = toUserDto(user);

		Mockito.when(service.update(userDto)).thenReturn(user);
		Mockito.when(mapper.toUserDto(user)).thenReturn(userDto);
		Mockito.when(mapper.fromUserDto(userDto)).thenReturn(user);

		String jsonString = asJsonString(userDto);
		System.out.println(jsonString);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/users")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonString);

		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(userDto))).andReturn();

		verify(this.service, Mockito.atLeastOnce()).update(userDto);
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String listAsJsonString(final List<UserDto> obj) {
		StringBuilder result = new StringBuilder("[");
		for (UserDto o : obj) {
			result.append(asJsonString(o)).append(",");
		}
		result = new StringBuilder(result.substring(0, result.length() - 1));
		result.append("]");
		return result.toString();
	}

	private UserDto toUserDto(User user) {
		UserDto dto = new UserDto();
		dto.setId(user.getId());
		dto.setEmail(user.getEmail());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setPassword(user.getPassword());
		dto.setRoles(user.getRoles().stream().map(this::toRoleDto).collect(Collectors.toList()));
		dto.setEnabled(user.isEnabled());
		return dto;
	}

	private RoleDto toRoleDto(Role role) {
		RoleDto dto = new RoleDto();
		dto.setId(role.getId());
		dto.setName(role.getName());
		return dto;
	}
}