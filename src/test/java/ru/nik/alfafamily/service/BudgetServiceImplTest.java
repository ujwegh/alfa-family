package ru.nik.alfafamily.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.nik.alfafamily.domain.Budget;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.repository.FamilyMemberRepository;
import ru.nik.alfafamily.repository.UserRepository;
import ru.nik.alfafamily.util.Utilities;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@EnableMongoRepositories(basePackages = {"ru.nik.alfafamily.repository"})
@EnableAutoConfiguration
@ContextConfiguration(classes = {CategoryServiceImpl.class, BudgetServiceImpl.class, FamilyMemberServiceImpl.class,
	UserServiceImpl.class, Mapper.class, FamilyMemberPropertiesServiceImpl.class, FinancialOperationServiceImpl.class})
class BudgetServiceImplTest {

	@Autowired
	private FinancialOperationService service;

	@Autowired
	private BudgetService budgetService;

	@Autowired
	private MongoTemplate template;

	@Autowired
	private Mapper mapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FamilyMemberRepository memberRepository;
	@BeforeEach
	public void init() {
		User user = new User("firstName", "secondName",
			"admin1@mail.com", "password");
		Role role = new Role("USER");
		template.save(role);
		user.setRoles(Collections.singleton(role));
		template.save(user);

		FamilyMember member1 = new FamilyMember("test-1-familyMember", user);
		List<FamilyMember> members = new ArrayList<>();
		members.add(member1);
		template.save(member1);
		user.setMembers(members);
		Category category1 = new Category("бензин", member1);
		Category category2 = new Category("продукты", member1);
		Category savedCategory1 = template.save(category1);
		Category savedCategory2 = template.save(category2);

		member1.setCategories(Arrays.asList(savedCategory1, savedCategory2));
		template.save(member1);

		FinancialOperation op1 = new FinancialOperation(new Date(), "расход",
			savedCategory1, 555.55, "RUB", 1234567890L,
			"оплата продуктов", "дороговато вышло");
		FinancialOperation op2 = new FinancialOperation(new Date(), "расход",
			savedCategory1, 999.99, "RUB", 1234567890L,
			"оплата пошлины", "опять");
		FinancialOperation op3 = new FinancialOperation(new Date(), "доход",
			savedCategory1, 999.99, "RUB", 1234567890L,
			"возврат пошлины", "ура");
		FinancialOperation op4 = new FinancialOperation(new Date(), "доход",
			savedCategory1, 1000.0, "RUB", 1234567890L,
			"возврат цены за торт", "ура!!!");
		template.save(op1);

		template.save(op2);
		template.save(op3);
		template.save(op4);
		member1.setCategories(Arrays.asList(savedCategory1, savedCategory2));
		template.save(member1);
	}

	@AfterEach
	public void cleanup() {
		template.getDb().drop();
	}

	@Test
	void countForAllTimeForUser() {//Budget countForAllTimeForUser(String userId);
		User user = userRepository.findAll().get(0);
		assertNotNull(user);
		List<FinancialOperation> operations = service.findAllForUser(user.getId());
		assertTrue(operations.size() > 0);
		Budget budget1 = Utilities.countBudget(operations);
		Budget budget = budgetService.countForAllTimeForUser(user.getId());
		assertNotNull(budget);
		assertEquals(budget.getIncome(), budget1.getIncome());
		assertEquals(budget.getOutcome(), budget1.getOutcome());
		assertEquals("1999.99", budget1.getIncome().toString());
		assertEquals("1555.54", budget1.getOutcome().toString());
	}

	@Test
	void countForAllTimeForFamilyMember() {
		// Budget countForAllTimeForFamilyMember(String userId, String memberId);
		FamilyMember familyMember = memberRepository.findAll().get(0);
		User user = familyMember.getUser();
		assertNotNull(user);

		List<FinancialOperation> operations = service.findAllForUser(user.getId());
		assertTrue(operations.size() > 0);
		Date start = operations.get(0).getDate();
		Date and = operations.get(operations.size() - 1).getDate();

		List<FinancialOperation> operations1 = service.findAllForFamilyMemberBetweenDates(user.getId(),familyMember.getId(), start,and);
		Budget budget1 = Utilities.countBudget(operations1);
		Budget budget = budgetService.countForAllTimeForUser(user.getId());
		assertNotNull(budget);
		assertEquals(budget.getIncome(), budget1.getIncome());
		assertEquals(budget.getOutcome(), budget1.getOutcome());
		assertEquals("1999.99", budget1.getIncome().toString());
		assertEquals("1555.54", budget1.getOutcome().toString());

	}

	@Test
	void countForUserBetweenDates() {
		// Budget countForUserBetweenDates(String userId, Date start, Date end);
		User user = userRepository.findAll().get(0);
		assertNotNull(user);
		List<FinancialOperation> operations = service.findAllForUser(user.getId());
		assertTrue(operations.size() > 0);
		Date start = operations.get(0).getDate();
		Date and = operations.get(operations.size() - 1).getDate();
		List<FinancialOperation> operationsBetweenDates = service.findAllForUserBetweenDates(user.getId(), start, and);
		assertTrue(operationsBetweenDates.size() > 0);
		Budget budget1 = Utilities.countBudget(operationsBetweenDates);
		Budget budget = budgetService.countForAllTimeForUser(user.getId());
		assertNotNull(budget);
		assertEquals(budget.getIncome(), budget1.getIncome());
		assertEquals(budget.getOutcome(), budget1.getOutcome());
		assertEquals("1999.99", budget1.getIncome().toString());
		assertEquals("1555.54", budget1.getOutcome().toString());
	}


	@Test
	void countForFamilyMemberBetweenDates() {
		// Budget countForFamilyMemberBetweenDates(String userId, String memberId, Date start, Date end);
		FamilyMember familyMember = memberRepository.findAll().get(0);
		User user = familyMember.getUser();
		assertNotNull(user);

		List<FinancialOperation> operations = service.findAllForUser(user.getId());
		assertTrue(operations.size() > 0);
		Date start = operations.get(0).getDate();
		Date and = operations.get(operations.size() - 1).getDate();

		List<FinancialOperation> operations1 = service.findAllForFamilyMemberBetweenDates(user.getId(),familyMember.getId(),start,and);
		Budget budget1 = Utilities.countBudget(operations1);
		Budget budget = budgetService.countForFamilyMemberBetweenDates(user.getId(),familyMember.getId(),start,and);
		assertNotNull(budget);
		assertEquals(budget.getIncome(), budget1.getIncome());
		assertEquals(budget.getOutcome(), budget1.getOutcome());
		assertEquals("1999.99", budget1.getIncome().toString());
		assertEquals("1555.54", budget1.getOutcome().toString());
	}
}