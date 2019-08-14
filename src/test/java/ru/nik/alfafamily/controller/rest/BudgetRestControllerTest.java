package ru.nik.alfafamily.controller.rest;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
import ru.nik.alfafamily.domain.Budget;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.BudgetDto;
import ru.nik.alfafamily.dto.CategoryDto;
import ru.nik.alfafamily.dto.FinancialOperationDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.security.AuthenticationSuccessHandlerImpl;
import ru.nik.alfafamily.security.AuthorizationComponent;
import ru.nik.alfafamily.service.BudgetService;
import ru.nik.alfafamily.service.UserService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BudgetRestController.class)
class BudgetRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private BudgetService service;

	@MockBean
	private Mapper mapper;

	@MockBean
	private UserService userService;

	@MockBean
	private AuthenticationSuccessHandlerImpl successHandler;

	private BudgetDto budgetDto;

	//  https://stackoverflow.com/questions/34592331/mockito-doesnt-mocking-with-preauthorize-and-spring-boot
	@org.springframework.boot.test.context.TestConfiguration
	protected static class TestConfiguration {

		@Bean("auth")
		@Primary
		public AuthorizationComponent getAuthorizationComponent() {
			return Mockito.spy(AuthorizationComponent.class);
		}
	}


	private User user;

	private Budget budget;

	private FamilyMember member;

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
		this.user = user;
		this.budget = new Budget();

		this.member = new FamilyMember("Mamba", user);
		this.member.setId("member111");
		Category category = new Category("Ненужные вещи", member);
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

		budget.setUserId(user.getId());
		budget.setFamilyMemberId(member.getId());
		budget.setStartDate(new Date(new Date().getTime() - 3000));
		budget.setEndDate(new Date(new Date().getTime() + 3000));
		budget.setIncome(1999.99);
		budget.setOutcome(1555.54);
		this.budgetDto = toBudgetDto(budget);
		Mockito.when(userService.findByEmail("email")).thenReturn(user);
	}

	@WithMockUser(username = "email")
	@Test
	void userBudget() throws Exception {
		Mockito.when(
			service.countForUserBetweenDates(user.getId(),
				new Date(new Date().getTime() - 3000),
				new Date(new Date().getTime() + 3000))).thenReturn(budget);
		Mockito.when(mapper.toBudgetDto(budget)).thenReturn(budgetDto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.post("/rest/{userId}/budget/count", "first111")
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.with(SecurityMockMvcRequestPostProcessors.user("email"))
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(budgetDto));

		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(budgetDto))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).countForUserBetweenDates(user.getId(),
			new Date(new Date().getTime() - 3000),
			new Date(new Date().getTime() + 3000));


	}

	@WithMockUser(username = "email")
	@Test
	void memberBudget() throws Exception {

		budget.setFamilyMemberId(null);
		Mockito.when(
			service.countForFamilyMemberBetweenDates(user.getId(), member.getId(),
				new Date(new Date().getTime() - 3000),
				new Date(new Date().getTime() + 3000))).thenReturn(budget);
		Mockito.when(mapper.toBudgetDto(budget)).thenReturn(budgetDto);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.post("/rest/{userId}/budget/count", "first111")
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.with(SecurityMockMvcRequestPostProcessors.user("email"))
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(budgetDto));

		this.mvc.perform(requestBuilder).andExpect(status().isOk())
			.andExpect(content().json(asJsonString(budgetDto))).andReturn();
		verify(this.service, Mockito.atLeastOnce()).countForUserBetweenDates(user.getId(),
			new Date(new Date().getTime() - 3000),
			new Date(new Date().getTime() + 3000));

	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String listAsJsonString(final List<BudgetDto> obj) {
		StringBuilder result = new StringBuilder("[");
		for (BudgetDto o : obj) {
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

	private BudgetDto toBudgetDto(Budget budget) {
		BudgetDto dto = new BudgetDto();
		dto.setUserId(budget.getUserId());
		dto.setFamilyMemberId(budget.getFamilyMemberId());
		dto.setIncome(budget.getIncome());
		dto.setOutcome(budget.getOutcome());
		dto.setStartDate(budget.getStartDate());
		dto.setEndDate(budget.getEndDate());
		return dto;
	}


}