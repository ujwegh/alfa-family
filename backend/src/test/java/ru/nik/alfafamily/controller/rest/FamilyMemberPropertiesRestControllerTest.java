package ru.nik.alfafamily.controller.rest;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import ru.nik.alfafamily.domain.FamilyMemberProperties;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.FamilyMemberPropertiesDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.security.AuthenticationSuccessHandlerImpl;
import ru.nik.alfafamily.security.AuthorizationServiceImpl;
import ru.nik.alfafamily.service.FamilyMemberPropertiesService;
import ru.nik.alfafamily.service.FamilyMemberService;
import ru.nik.alfafamily.service.UserService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FamilyMemberPropertiesRestController.class)
class FamilyMemberPropertiesRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private FamilyMemberPropertiesService service;

	@MockBean
	private Mapper mapper;

	@MockBean
	private UserService userService;

	@MockBean
	private FamilyMemberService memberService;

	@MockBean
	private AuthenticationSuccessHandlerImpl successHandler;

	private List<FamilyMemberProperties> famMembProperties = new ArrayList<>();

	private List<FamilyMemberPropertiesDto> famMembPropDtos = new ArrayList<>();

	private FamilyMember member;

	//  https://stackoverflow.com/questions/34592331/mockito-doesnt-mocking-with-preauthorize-and-spring-boot
	@org.springframework.boot.test.context.TestConfiguration
	protected static class TestConfiguration {

		@Bean("auth")
		@Primary
		public AuthorizationServiceImpl getAuthorizationService() {
			return Mockito.spy(AuthorizationServiceImpl.class);
		}
	}

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

		FamilyMemberProperties famMemProperties = new FamilyMemberProperties(member, "blue");
		famMemProperties.setId("fam_mem_prop_1");

		famMembProperties.add(famMemProperties);
		famMembPropDtos.add(toFamilyMemberPropertiesDto(famMemProperties));
		Mockito.when(userService.findByEmail("email")).thenReturn(user);
	}

	@WithMockUser(username = "email")
	@Test
	void update() throws Exception {
		FamilyMemberProperties familyMemberProperties = famMembProperties.get(0);
		familyMemberProperties.setColor("Red");
		FamilyMemberPropertiesDto dto = toFamilyMemberPropertiesDto(familyMemberProperties);

		Map<String, String> map = new HashMap<>();
		map.put("color", famMembPropDtos.get(0).getColor());
		Mockito.when(service.createOrUpdate(member.getId(), map)).thenReturn(familyMemberProperties);
		Mockito.when(mapper.toFamilyMemberPropertiesDto(familyMemberProperties))
			.thenReturn(dto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/rest/{userId}/properties", "first111")
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(dto));
		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(dto))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).createOrUpdate(member.getId(), map);
	}

	@WithMockUser(username = "email")
	@Test
	void delete() throws Exception {
		Mockito.when(service.delete(member.getId())).thenReturn(true);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(
			"/rest/{userId}/properties/{familyMemberId}","first111", "member111");
		this.mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		verify(this.service, Mockito.atLeastOnce()).delete(member.getId());
	}

	@WithMockUser(username = "email")
	@Test
	void get() throws Exception {
		FamilyMemberProperties property = famMembProperties.get(0);
		FamilyMemberPropertiesDto dto = famMembPropDtos.get(0);

		Mockito.when(mapper.toFamilyMemberPropertiesDto(property)).thenReturn(dto);
		Mockito.when(service.findByFamilyMemberId("member111")).thenReturn(property);

		Mockito.when(mapper.fromFamilyMemberPropertiesDto(dto)).thenReturn(property);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.get("/rest/{userId}/properties/{familyMemberId}", "first111", "member111")
			.accept(MediaType.APPLICATION_JSON_VALUE);
		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(dto))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).findByFamilyMemberId(member.getId());
	}


	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public FamilyMemberPropertiesDto toFamilyMemberPropertiesDto(
		FamilyMemberProperties properties) {
		if (properties == null) return null;
		FamilyMemberPropertiesDto dto = new FamilyMemberPropertiesDto();
		dto.setColor(properties.getColor());
		dto.setFamilyMemberId(properties.getFamilyMember().getId());
		dto.setId(properties.getId());
		return dto;
	}


}