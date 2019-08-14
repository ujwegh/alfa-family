package ru.nik.alfafamily.controller.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
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
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.CategoryDto;
import ru.nik.alfafamily.dto.FamilyMemberDto;
import ru.nik.alfafamily.dto.FinancialOperationDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.security.AuthenticationSuccessHandlerImpl;
import ru.nik.alfafamily.security.AuthorizationComponent;
import ru.nik.alfafamily.service.CategoryService;
import ru.nik.alfafamily.service.FinancialOperationService;
import ru.nik.alfafamily.service.UserService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FinancialOperationRestController.class)
class FinancialOperationRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService userService;

	@MockBean
	private Mapper mapper;

	@MockBean
	private AuthenticationSuccessHandlerImpl successHandler;

	@MockBean
	private CategoryService categoryService;

	@MockBean
	private FinancialOperationService service;


	//	https://stackoverflow.com/questions/34592331/mockito-doesnt-mocking-with-preauthorize-and-spring-boot
	@org.springframework.boot.test.context.TestConfiguration
	protected static class TestConfiguration {

		@Bean("auth")
		@Primary
		public AuthorizationComponent getAuthorizationComponent() {
			return Mockito.spy(AuthorizationComponent.class);
		}
	}


	private FamilyMember member;

	private Category category;

	private List<FinancialOperation> operations = new ArrayList<>();

	private List<FinancialOperationDto> operationDtos = new ArrayList<>();


	@BeforeEach
	void init() {
		User user = new User();
		user.setId("first111");
		user.setFirstName("Firstname");
		user.setLastName("Lastname");
		user.setEmail("email");
		user.setPassword("password");
		user.setEnabled(true);
		user.setRoles(Collections.singleton(new Role("ROLE_USER")));

		this.member = new FamilyMember("Rumba", user);
		this.member.setId("member111");
		this.category = new Category("Ненужные вещи", member);
		category.setId("category111");

		FinancialOperation op1 = new FinancialOperation("op1", new Date(), "расход",
			category, 555.55, "RUB", 1234567890L,
			"оплата продуктов", "дороговато вышло");
		FinancialOperation op2 = new FinancialOperation("op2", new Date(), "расход",
			category, 999.99, "RUB", 1234567890L,
			"оплата пошлины", "опять");
		FinancialOperation op3 = new FinancialOperation("op3", new Date(), "доход",
			category, 999.99, "RUB", 1234567890L,
			"возврат пошлины", "ура");
		FinancialOperation op4 = new FinancialOperation("op4", new Date(), "доход",
			category, 1000.0, "RUB", 1234567890L,
			"возврат цены за торт", "ура!!!");

		operations.addAll(Arrays.asList(op1, op2, op3, op4));
		operations.forEach(operation -> operationDtos.add(toFinancialOperationDto(operation)));

		Mockito.when(userService.findByEmail("email")).thenReturn(user);
	}


	@WithMockUser(username = "email")
	@Test
	void findAllForUser() throws Exception {
		Mockito.when(service.findAllForUser("first111")).thenReturn(Collections.singletonList(operations.get(0)));
		Mockito.when(mapper.toFinancialOperationDto(operations.get(0))).thenReturn(toFinancialOperationDto(operations.get(0)));

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.get("/rest/{userId}/finoperations", "first111")
			.accept(MediaType.APPLICATION_JSON_VALUE);
		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(listAsJsonString(Collections.singletonList(operationDtos.get(0)))))
			.andReturn();
		verify(this.service, Mockito.atLeastOnce()).findAllForUser("first111");

	}

	@WithMockUser(username = "email")
	@Test
	void delete() throws Exception {
		Mockito.when(service.delete("op1")).thenReturn(true);
		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.delete("/rest/{userId}/finoperations/{operationId}", "first111", "op1")
			.with(csrf());
		this.mvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
		verify(this.service, Mockito.atLeastOnce()).delete("op1");
	}

	@WithMockUser(username = "email")
	@Test
	void create() throws Exception {
		FinancialOperation newOp =  new FinancialOperation("op5", new Date(), "расход",
			category, 300.55, "RUB", 1234567890L,
			"оплата чего-то еще", "все плохо");
		newOp.setPlanned(true);
		FinancialOperationDto dto = toFinancialOperationDto(newOp);

		Mockito.when(service.create(dto)).thenReturn(newOp);
		Mockito.when(mapper.toFinancialOperationDto(newOp)).thenReturn(dto);
		Mockito.when(mapper.fromFinancialOperationDto(dto)).thenReturn(newOp);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.post("/rest/{userId}/finoperations", "first111")
			.with(csrf()).contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(asJsonString(dto));

		;
		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(dto))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).create(dto);
	}

	@WithMockUser(username = "email")
	@Test
	void findById() throws Exception {
		Mockito.when(service.findById("op1")).thenReturn(operations.get(0));
		Mockito.when(mapper.toFinancialOperationDto(operations.get(0))).thenReturn(toFinancialOperationDto(operations.get(0)));

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.get("/rest/{userId}/finoperations/{operationId}", "first111", "op1")
			.accept(MediaType.APPLICATION_JSON_VALUE);
		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(operationDtos.get(0)))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).findById("op1");
	}

	@WithMockUser(username = "email")
	@Test
	void createFromCsv() {

	}

	@WithMockUser(username = "email")
	@Test
	void userOperationsBetween() {

	}

	@WithMockUser(username = "email")
	@Test
	void memberOperationsBetween() {

	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String listAsJsonString(final List<FinancialOperationDto> obj) {
		StringBuilder result = new StringBuilder("[");
		for (FinancialOperationDto o : obj) {
			result.append(asJsonString(o)).append(",");
		}
		result = new StringBuilder(result.substring(0, result.length() - 1));
		result.append("]");
		return result.toString();
	}




	public FinancialOperationDto toFinancialOperationDto(FinancialOperation operation) {
		FinancialOperationDto dto = new FinancialOperationDto();
		dto.setId(operation.getId());
		dto.setComment(operation.getComment());
		dto.setDescription(operation.getDescription());
		dto.setCurrency(operation.getCurrency());
		dto.setSum(operation.getSum());
		dto.setAccountNumber(operation.getAccountNumber());
		dto.setType(operation.getType());
		dto.setDate(operation.getDate());
		dto.setPlanned(operation.isPlanned());
		dto.setCategory(toCategoryDto(operation.getCategory()));
		return dto;
	}

	public CategoryDto toCategoryDto(Category category) {
		CategoryDto dto = new CategoryDto();
		dto.setName(category.getName());
		dto.setId(category.getId());
		dto.setFamilyMemberId(category.getFamilyMember().getId());
		return dto;
	}

}