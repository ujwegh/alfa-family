package ru.nik.alfafamily.controller.rest;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.nik.alfafamily.domain.Budget;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.BudgetDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.service.BudgetService;

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
	private List<Budget> budgets = new ArrayList<>();

	private List<BudgetDto> budgetDto = new ArrayList<>();

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
		Budget budget = new Budget();
		budget.setUserId(user.getId());

		budgetDto.add(toBudgetDto(budget));
	}

	@WithMockUser()
	@Test
	void userBudget() {



	}

	@WithMockUser()
	@Test
	void memberBudget() {



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
}