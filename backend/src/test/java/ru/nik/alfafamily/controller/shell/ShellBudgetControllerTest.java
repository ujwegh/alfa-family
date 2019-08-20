package ru.nik.alfafamily.controller.shell;

import static org.junit.jupiter.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.annotation.DirtiesContext;
import ru.nik.alfafamily.domain.Budget;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.dto.UserRegistrationDto;
import ru.nik.alfafamily.service.BudgetService;
import ru.nik.alfafamily.service.CategoryService;
import ru.nik.alfafamily.service.FamilyMemberService;
import ru.nik.alfafamily.service.FinancialOperationService;
import ru.nik.alfafamily.service.UserService;

@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@EnableMongoRepositories(basePackages = {"ru.nik.alfafamily.repository"})
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShellBudgetControllerTest {

	@Autowired
	private UserService userService;

	@Autowired
	private BudgetService service;

	@Autowired
	private FamilyMemberService memberService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private FinancialOperationService operationService;

	@Autowired
	private ShellBudgetController controller;
	@Autowired
	private Mapper mapper;

	@BeforeEach
	void init() {
		controller = new ShellBudgetController(service);
		UserRegistrationDto registrationDto = new UserRegistrationDto();
		registrationDto.setFirstName("Firstname");
		registrationDto.setLastName("Lastname");
		registrationDto.setEmail("email");
		registrationDto.setConfirmEmail("email");
		registrationDto.setPassword("password");
		registrationDto.setConfirmPassword("password");
		registrationDto.setTerms(true);
		User user = userService.save(registrationDto);

		FamilyMember member = memberService.create(user.getId(), "Member");
		memberService.create(user.getId(), member.getName());
		Category category = categoryService.create(member.getId(), "some-category");
		FinancialOperation op1 = new FinancialOperation(new Date(), "расход",
			category, 555.55, "RUB", 1234567890L,
			"оплата продуктов", "дороговато вышло");
		FinancialOperation op2 = new FinancialOperation(new Date(), "расход",
			category, 999.99, "RUB", 1234567890L,
			"оплата пошлины", "опять");
		FinancialOperation op3 = new FinancialOperation(new Date(), "доход",
			category, 999.99, "RUB", 1234567890L,
			"возврат пошлины", "ура");
		FinancialOperation op4 = new FinancialOperation(new Date(), "доход",
			category, 1000.0, "RUB", 1234567890L,
			"возврат цены за торт", "ура!!!");
		operationService.create(mapper.toFinancialOperationDto(op1));
		operationService.create(mapper.toFinancialOperationDto(op2));
		operationService.create(mapper.toFinancialOperationDto(op3));
		operationService.create(mapper.toFinancialOperationDto(op4));
	}

	@Test
	void user_budget() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);
		assertNotNull(familyMember);
		String s = controller.user_budget(user.getId());
		Budget budget = service.countForAllTimeForUser(user.getId());
		assertNotNull(s);
		assertEquals(budget.toString(), s);
	}

	@Test
	void family_member_budget() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);
		assertNotNull(familyMember);
		String s = controller.family_member_budget(user.getId(), familyMember.getId());
		Budget budget = service.countForAllTimeForFamilyMember(user.getId(), familyMember.getId());
		assertNotNull(s);
		assertEquals(budget.toString(), s);

	}

	@Test
	void user_budget_between() {
		User user = userService.findAll().get(0);

		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);
		assertNotNull(familyMember);

		List<FinancialOperation> operations = operationService.findAllForUser(user.getId());
		assertTrue(operations.size() > 0);

		Date start = operations.get(0).getDate();
		Date end = operations.get(operations.size() - 1).getDate();


		String s = controller.user_budget_between(user.getId(), parseDate(start), parseDate(end));
		Budget budget = service.countForUserBetweenDates(user.getId(), start, end);
		assertNotNull(s);
		assertEquals(budget.toString(), s);
	}

	@Test
	void family_member_budget_between() {
		User user = userService.findAll().get(0);

		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);
		assertNotNull(familyMember);

		List<FinancialOperation> operations = operationService.findAllForUser(user.getId());
		assertTrue(operations.size() > 0);

		Date start = operations.get(0).getDate();
		Date end = operations.get(operations.size() - 1).getDate();

		String s = controller.family_member_budget_between(user.getId(), familyMember.getId(),
			parseDate(start), parseDate(end));
		Budget budget = service.countForFamilyMemberBetweenDates(user.getId(),familyMember.getId(), start, end);
		assertNotNull(s);
		assertEquals(budget.toString(), s);
	}


	private String parseDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		return format.format(date);
	}
}