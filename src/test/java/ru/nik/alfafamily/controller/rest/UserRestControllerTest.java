package ru.nik.alfafamily.controller.rest;

import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.nik.alfafamily.controller.shell.ShellUserController;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.dto.RoleDto;
import ru.nik.alfafamily.dto.UserDto;
import ru.nik.alfafamily.dto.UserRegistrationDto;
import ru.nik.alfafamily.repository.RoleRepository;
import ru.nik.alfafamily.security.AuthenticationSuccessHandlerImpl;
import ru.nik.alfafamily.service.FamilyMemberService;
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
	private RoleRepository repository;

	@MockBean
	private FamilyMemberService familyMemberService;

	@MockBean
	private AuthenticationSuccessHandlerImpl successHandler;

	private List<User> users = new ArrayList<>();

	private List<UserDto> userDtos = new ArrayList<>();

	private UserRegistrationDto dto;

	@BeforeEach
	void init() {
		UserRegistrationDto registrationDto = new UserRegistrationDto();
		registrationDto.setFirstName("Firstname");
		registrationDto.setLastName("Lastname");
		registrationDto.setEmail("email");
		registrationDto.setConfirmEmail("email");
		registrationDto.setPassword("password");
		registrationDto.setConfirmPassword("password");
		registrationDto.setTerms(true);

		dto = registrationDto;

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

	@Test
	void findById() {

	}

	@Test
	void findByEmail() {

	}

	@Test
	void delete() {

	}

	@Test
	void create() {
		UserRegistrationDto registrationDto = new UserRegistrationDto();
		registrationDto.setFirstName("qwe");
		registrationDto.setLastName("qwe");
		registrationDto.setEmail("qwe@aa.ru");
		registrationDto.setConfirmEmail("qwe@aa.ru");
		registrationDto.setPassword("passqweword");
		registrationDto.setConfirmPassword("passqweword");
		registrationDto.setTerms(true);

		Mockito.when(service.save(registrationDto)).thenReturn(users.get(0));

//		expected.add(new Author("Лермонтов"));
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/rest/authors").with(csrf())
//			.contentType(MediaType.APPLICATION_JSON)
//			.content(asJsonString(new AuthorDto(0,"Лермонтов", Collections.emptyList())));
//		this.mvc.perform(requestBuilder).andExpect(status().isOk())
//			.andExpect(content().json("{\"id\":0,\"name\":\"Лермонтов\",\"bookNames\":[]}")).andReturn();
//		verify(this.service, Mockito.atLeastOnce()).addAuthor( "Лермонтов");
	}

	@Test
	void update() {

	}











	private static String asJsonString(final UserDto obj) {
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

	private User fromUserDto(UserDto dto) {
		User user = new User();
		user.setId(dto.getId());
		user.setFirstName(dto.getFirstName());
		user.setLastName(dto.getLastName());
		user.setEmail(dto.getEmail());
		user.setPassword(dto.getPassword());
		user.setEnabled(dto.isEnabled());
		user.setRoles(dto.getRoles().stream().map(this::fromRoleDto).collect(Collectors.toList()));
		return user;
	}

	private RoleDto toRoleDto(Role role) {
		RoleDto dto = new RoleDto();
		dto.setId(role.getId());
		dto.setName(role.getName());
		return dto;
	}

	private Role fromRoleDto(RoleDto dto) {
		Role role = new Role();
		role.setId(dto.getId());
		role.setName(dto.getName());
		return role;
	}
}