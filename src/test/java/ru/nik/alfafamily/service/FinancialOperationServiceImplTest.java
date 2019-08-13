package ru.nik.alfafamily.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.FinancialOperationDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.repository.CategoryRepository;
import ru.nik.alfafamily.repository.FamilyMemberRepository;
import ru.nik.alfafamily.repository.FinancialOperationRepository;
import ru.nik.alfafamily.repository.UserRepository;
import ru.nik.alfafamily.util.Utilities;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@EnableMongoRepositories(basePackages = {"ru.nik.alfafamily.repository"})
@EnableAutoConfiguration
@ContextConfiguration(classes = {FinancialOperationServiceImpl.class,
	UserServiceImpl.class, FamilyMemberServiceImpl.class, Mapper.class, CategoryServiceImpl.class,
	FamilyMemberPropertiesServiceImpl.class})
class FinancialOperationServiceImplTest {

	@Autowired
	private FinancialOperationService service;

	@Autowired
	private CategoryRepository categoryRepository;

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
			"admin@mail.com", "password");
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

//		member1.setCategories(Arrays.asList(savedCategory1, savedCategory2));
//		template.save(member1);

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
//		member1.setCategories(Arrays.asList(savedCategory1, savedCategory2));
//		template.save(member1);
	}

	@AfterEach
	public void cleanup() {
		template.getDb().drop();
	}

	@Test
	void createCSV() throws IOException {
		FamilyMember familyMember = memberRepository.findAll().get(0);
		File file = new ClassPathResource("Budget_2019-07-01-2019-07-31.csv").getFile();

		List<FinancialOperation> operations = service
			.createOrUpdate(familyMember.getId(), Utilities.convertToMultipartFile(file));

		operations.forEach(operation -> System.out.println(operation.toString() + "\n"));

		assertNotNull(operations);
		assertTrue(operations.size() > 0);

	}

	@Test
	void updateCSV() {
//  Not implemented yet
	}

	@Test
	void findAllForUser() { //List<FinancialOperation>

		User user = userRepository.findAll().get(0);
		List<Category> categories = categoryRepository.findAll();

		FinancialOperation op1 = new FinancialOperation(new Date(), "расход",
			categories.get(0), 999.99, "RUB", 1234567890L,
			"оплата пошлины", "опять");
		FinancialOperation op2 = new FinancialOperation(new Date(), "доход",
			categories.get(0), 999.99, "RUB", 1234567890L, "возврат пошлины", "ура");
		FinancialOperation op3 = new FinancialOperation(new Date(), "доход",
			categories.get(0), 1000.0, "RUB", 1234567890L,
			"возврат цены за торт", "ура!!!");
		List<FinancialOperation> operations = new ArrayList<>();
		operations.add(op1);
		operations.add(op2);
		operations.add(op3);

		List<FinancialOperation> expected = service.findAllForUser(user.getId());
		assertNotNull(expected);
		assertTrue(expected.size() > 0);
		assertNotNull(expected);
		assertEquals(operations.size(), expected.size() - 1);
	}

	@Test
	void deleteAllForFamily0Member() { //Boolean
		FamilyMember familyMember = memberRepository.findAll().get(0);
		User user = familyMember.getUser();
		boolean b = service.deleteAllForFamilyMember(user.getId(), familyMember.getId());
		assertTrue(b);
	}

	@Test
	void findAllForUserBetweenDates() { //List<FinancialOperation>
		User user = userRepository.findAll().get(0);
		List<FinancialOperation> operations = service.findAllForUser(user.getId());
		Date start = operations.get(0).getDate();
		Date and = operations.get(operations.size() - 1).getDate();
		List<FinancialOperation> expected = service
			.findAllForUserBetweenDates(user.getId(), start, and);
		assertNotNull(expected);
		assertTrue(expected.size() > 0);
		assertEquals(4, operations.size());
		assertEquals(4, expected.size());
	}

	@Test
	void findAllForFamilyMemberBetween0Dates() { //List<FinancialOperation>
		FamilyMember familyMember = memberRepository.findAll().get(0);
		User user = familyMember.getUser();
		List<FinancialOperation> operations = service.findAllForUser(user.getId());
		Date start = operations.get(0).getDate();
		Date and = operations.get(operations.size() - 1).getDate();
		List<FinancialOperation> expected = service
			.findAllForFamilyMemberBetweenDates(user.getId(), familyMember.getId(), start, and);
		assertNotNull(expected);
		assertTrue(expected.size() > 0);
		assertEquals(4, operations.size());
		assertEquals(4, expected.size());

	}

	@Test
	void create() {
		Category category = categoryRepository.findAll().get(0);
		FinancialOperation expected = new FinancialOperation(new Date(), "расход",
			category, 999.99, "RUB", 1234567890L,
			"оплата пошлины", "опять");

		FinancialOperation actual = service.create(mapper.toFinancialOperationDto(expected));

		assertNotNull(actual);
		assertNotNull(actual.getCategory());
		assertEquals(expected.getDescription(), actual.getDescription());
	}

	@Test
	void update() {
		Category category = categoryRepository.findAll().get(0);
		FinancialOperation expected0 = new FinancialOperation(new Date(), "расход",
			category, 999.99, "RUB", 1234567890L,
			"оплата пошлины", "опять");

		FinancialOperation expected = service.create(mapper.toFinancialOperationDto(expected0));
		FinancialOperation actual = service.update(mapper.toFinancialOperationDto(expected));
		assertNotNull(actual);
		assertNotNull(actual.getCategory());
		assertEquals(expected.getDescription(), actual.getDescription());
	}

	@Test
	void delete() {
		Category category = categoryRepository.findAll().get(0);
		FinancialOperation expected0 = new FinancialOperation(new Date(), "расход",
			category, 999.99, "RUB", 1234567890L,
			"оплата пошлины", "опять");

		FinancialOperation expected = service.create(mapper.toFinancialOperationDto(expected0));
		boolean b = service.delete(expected.getId());
		assertNotNull(b);
		assertTrue(b);
	}

	@Test
	void findById() {
		Category category = categoryRepository.findAll().get(0);
		FinancialOperation expected0 = new FinancialOperation(new Date(), "расход", category,
			999.99, "RUB", 1234567890L, "оплата пошлины", "опять");

		FinancialOperation expected = service.create(mapper.toFinancialOperationDto(expected0));
		FinancialOperation actual = service.findById(expected.getId());
		assertNotNull(actual);
		assertNotNull(actual.getCategory());
		assertEquals(expected.getDescription(), actual.getDescription());
	}
}
