package ru.nik.alfafamily.controller.rest;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.CategoryDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.security.AuthenticationSuccessHandlerImpl;
import ru.nik.alfafamily.security.AuthorizationComponent;
import ru.nik.alfafamily.service.CategoryService;
import ru.nik.alfafamily.service.UserService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CategoryRestController.class)
class CategoryRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private CategoryService service;

	@MockBean
	private Mapper mapper;

	@MockBean
	private UserService userService;

	@MockBean
	private AuthenticationSuccessHandlerImpl successHandler;

	private FamilyMember member;

	private Category category;

	private List<Category> categories = new ArrayList<>();

	private List<CategoryDto> categoryDtos = new ArrayList<>();

	private List<FamilyMember> familyMembers = new ArrayList<>();

	//  https://stackoverflow.com/questions/34592331/mockito-doesnt-mocking-with-preauthorize-and-spring-boot
	@org.springframework.boot.test.context.TestConfiguration
	protected static class TestConfiguration {

		@Bean("auth")
		@Primary
		public AuthorizationComponent getAuthorizationComponent() {
			return Mockito.spy(AuthorizationComponent.class);
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
		this.member.setId("fam.memb.1");

		this.category = new Category("продукты", member);
		this.category.setId("cat1");

		familyMembers.add(member);
		categories.add(category);
		categoryDtos.add(toCategoryDto(category));

		Mockito.when(userService.findByEmail("email")).thenReturn(user);
	}

	@WithMockUser(username = "email")
	@Test
	void findAll() throws Exception {
		Category category = new Category();
		category.setName("Развлечения");
		category.setFamilyMember(familyMembers.get(0));
		categories.add(category);
		Mockito.when(service.findAll(familyMembers.get(0).getId()))
			.thenReturn(Collections.singletonList(categories.get(0)));
		Mockito.when(mapper.toCategoryDto(categories.get(0))).thenReturn(categoryDtos.get(0));

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.get("/rest/{userId}/categories/{familyMemberId}", "first111", "fam.memb.1")
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.with(SecurityMockMvcRequestPostProcessors.user("email"));
		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(
				content().json(listAsJsonString(Collections.singletonList(categoryDtos.get(0)))))
			.andReturn();
		verify(this.service, Mockito.atLeastOnce()).findAll(familyMembers.get(0).getId());
	}

	@WithMockUser(username = "email")
	@Test
	void findById() throws Exception {
		Mockito.when(service.findById("cat1")).thenReturn(categories.get(0));
		Mockito.when(mapper.toCategoryDto(categories.get(0))).thenReturn(categoryDtos.get(0));
		CategoryDto dto = categoryDtos.get(0);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.get("/rest/{userId}/categories/category/{categoryId}", "first111", "cat1")
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.with(SecurityMockMvcRequestPostProcessors.user("email"));
		mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(dto))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).findById("cat1");
	}

	@WithMockUser(username = "email")
	@Test
	void findByName() throws Exception {
		Mockito.when(service.findByName("fam.memb.1", "продукты")).thenReturn(categories.get(0));
		Mockito.when(mapper.toCategoryDto(categories.get(0))).thenReturn(categoryDtos.get(0));
		CategoryDto dto = categoryDtos.get(0);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.get("/rest/{userId}/categories/{familyMemberId}/category/{name}",
				"first111", "fam.memb.1", "продукты")
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.with(SecurityMockMvcRequestPostProcessors.user("email"));
		mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(dto))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).findByName("fam.memb.1", "продукты");
	}

	@WithMockUser(username = "email")
	@Test
	void create() throws Exception {
		Category newCategory = new Category("new-category", member);
		newCategory.setId("cat2");

		CategoryDto categoryDto = toCategoryDto(newCategory);
		categoryDto.setFamilyMemberId(familyMembers.get(0).getId());
		categoryDto.setId(categories.get(0).getId());

		Mockito.when(service.create(familyMembers.get(0).getId(), "cat2")).thenReturn(newCategory);
		Mockito.when(mapper.toCategoryDto(newCategory)).thenReturn(categoryDto);
		Mockito.when(mapper.fromCategoryDto(categoryDto)).thenReturn(newCategory);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.post("/rest/{userId}/categories", "first111")
			.with(SecurityMockMvcRequestPostProcessors.user("email"))
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(categoryDto));
		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(categoryDto))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).create(familyMembers.get(0).getId(), "cat2");
	}

	@WithMockUser(username = "email")
	@Test
	void update() throws Exception {
		Category category = categories.get(0);
		category.setFamilyMember(familyMembers.get(0));
		CategoryDto categoryDto = toCategoryDto(category);

		Mockito.when(service.findByName("fam.memb.1", "продукты")).thenReturn(category);
		Mockito.when(mapper.toCategoryDto(category)).thenReturn(toCategoryDto(category));
		Mockito.when(mapper.fromCategoryDto(categoryDto)).thenReturn(category);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.put("/rest/{userId}/categories", "first111")
			.with(SecurityMockMvcRequestPostProcessors.user("email"))
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(toCategoryDto(category)));
		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(categoryDto))).andReturn();
		verify(this.service, Mockito.atLeastOnce())
			.updateById(category.getId(), "продукты");
	}

	@WithMockUser(username = "email")
	@Test
	void delete() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(
			"/rest/{userId}/categories/{familyMemberId}/category/{name}",
			"first111", "fam.memb.1", "cat1").with(SecurityMockMvcRequestPostProcessors
			.user("email"));
		this.mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		verify(this.service, Mockito.atLeastOnce())
			.deleteByName(familyMembers.get(0).getId(), "cat1");
	}

	public CategoryDto toCategoryDto(Category category) {
		CategoryDto dto = new CategoryDto();
		dto.setName(category.getName());
		dto.setId(category.getId());
		dto.setFamilyMemberId(category.getFamilyMember().getId());
		return dto;
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String listAsJsonString(final List<CategoryDto> obj) {
		StringBuilder result = new StringBuilder("[");
		for (CategoryDto o : obj) {
			result.append(asJsonString(o)).append(",");
		}
		result = new StringBuilder(result.substring(0, result.length() - 1));
		result.append("]");
		return result.toString();
	}
}