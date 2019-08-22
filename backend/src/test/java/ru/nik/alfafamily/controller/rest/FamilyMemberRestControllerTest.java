package ru.nik.alfafamily.controller.rest;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.FamilyMemberDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.security.AuthenticationSuccessHandlerImpl;
import ru.nik.alfafamily.security.AuthorizationServiceImpl;
import ru.nik.alfafamily.service.FamilyMemberService;
import ru.nik.alfafamily.service.UserService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FamilyMemberRestController.class)
class FamilyMemberRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService userService;

	@MockBean
	private Mapper mapper;

	@MockBean
	private AuthenticationSuccessHandlerImpl successHandler;

	@MockBean
	private FamilyMemberService service;

	//	https://stackoverflow.com/questions/34592331/mockito-doesnt-mocking-with-preauthorize-and-spring-boot
	@org.springframework.boot.test.context.TestConfiguration
	protected static class TestConfiguration {

		@Bean("auth")
		@Primary
		public AuthorizationServiceImpl getAuthorizationService() {
			return Mockito.spy(AuthorizationServiceImpl.class);
		}
	}

	private User user;

	private FamilyMember member;

	private FamilyMemberDto dto;

	@BeforeEach
	void init() {
		User user = new User();
		user.setId("first111");
		user.setFirstName("Firstname");
		user.setLastName("Lastname");
		user.setEmail("email");
		user.setPassword("password");
		user.setEnabled(true);
		user.setRole(new Role("ROLE_USER"));

		this.member = new FamilyMember("Mamba", user);
		this.member.setId("member111");
		this.user = user;
		this.dto = toFamilyMemberDto(member);

		Mockito.when(userService.findByEmail("email")).thenReturn(user);
	}


	@WithMockUser(username = "email")
	@Test
	void getAll() throws Exception {
		Mockito.when(service.findAll("first111")).thenReturn(Collections.singletonList(member));
		Mockito.when(mapper.toFamilyMemberDto(member)).thenReturn(dto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.get("/rest/{userId}/family", "first111")
			.accept(MediaType.APPLICATION_JSON_VALUE);
		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(listAsJsonString(Collections.singletonList(dto))))
			.andReturn();
		verify(this.service, Mockito.atLeastOnce()).findAll("first111");
	}

	@WithMockUser(username = "email")
	@Test
	void findById() throws Exception {
		Mockito.when(service.findById("member111")).thenReturn(member);
		Mockito.when(mapper.toFamilyMemberDto(member)).thenReturn(dto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.get("/rest/{userId}/family/member/{familyMemberId}", "first111", "member111")
			.accept(MediaType.APPLICATION_JSON_VALUE);
		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(dto))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).findById("member111");
	}

	@WithMockUser(username = "email")
	@Test
	void delete() throws Exception {
		Mockito.when(service.delete("member111")).thenReturn(true);
		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.delete("/rest/{userId}/family/member/{familyMemberId}", "first111", "member111")
			.with(csrf());
		this.mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		verify(this.service, Mockito.atLeastOnce()).delete("member111");
	}

	@WithMockUser(username = "email")
	@Test
	void update() throws Exception {
		FamilyMember toUpdateMember = member;
		toUpdateMember.setName("Jack");

		FamilyMemberDto memberDto = toFamilyMemberDto(toUpdateMember);

		Mockito.when(service.update(toUpdateMember.getId(), "Jack")).thenReturn(toUpdateMember);
		Mockito.when(mapper.toFamilyMemberDto(toUpdateMember)).thenReturn(memberDto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.put("/rest/{userId}/family/member", "first111")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(memberDto));
		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(memberDto))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).update(toUpdateMember.getId(), "Jack");

	}

	@WithMockUser(username = "email")
	@Test
	void create() throws Exception {
		FamilyMember newMember = new FamilyMember("Newmba", user);
		FamilyMemberDto memberDto = toFamilyMemberDto(newMember);

		Mockito.when(service.create(user.getId(), newMember.getName())).thenReturn(newMember);
		Mockito.when(mapper.toFamilyMemberDto(newMember)).thenReturn(memberDto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.post("/rest/{userId}/family", "first111")
			.with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(memberDto));

		System.out.println(asJsonString(memberDto));
		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(memberDto))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).create(user.getId(), newMember.getName());
	}


	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String listAsJsonString(final List<FamilyMemberDto> obj) {
		StringBuilder result = new StringBuilder("[");
		for (FamilyMemberDto o : obj) {
			result.append(asJsonString(o)).append(",");
		}
		result = new StringBuilder(result.substring(0, result.length() - 1));
		result.append("]");
		return result.toString();
	}

	private FamilyMemberDto toFamilyMemberDto(FamilyMember familyMember) {
		FamilyMemberDto dto = new FamilyMemberDto();
		dto.setId(familyMember.getId());
		dto.setName(familyMember.getName());
		dto.setUserId(familyMember.getUser().getId());
		return dto;
	}
}