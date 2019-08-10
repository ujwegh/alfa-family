package ru.nik.alfafamily.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Date;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@EnableMongoRepositories(basePackages = {"ru.nik.alfafamily.repository"})
@EnableAutoConfiguration
@ContextConfiguration(classes = {FinancialOperationServiceImpl.class,
	UserServiceImpl.class, FamilyMemberServiceImpl.class, Mapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
		template.save(member1);

		Category category1 = new Category("бензин", member1);
		Category category2 = new Category("продукты", member1);
		Category savedCategory1 = template.save(category1);
		Category savedCategory2 = template.save(category2);

		FinancialOperation operation = new FinancialOperation(new Date(), "расход",
			savedCategory1, 555.55, "RUB", 1234567890L,
			"оплата продуктов", "дороговато вышло");

		template.save(operation);
	}

	@AfterEach
	public void cleanup() {
		template.getDb().drop();
	}


	@Test
	void createOrUpdate() {
		User user = userRepository.findAll().get(0);
	//	FamilyMember familyMember = memberRepository.
		Category category = categoryRepository.findAll().get(0);
		FinancialOperation expected = new FinancialOperation(new Date(), "расход",
				category, 999.99, "RUB", 1234567890L,
				"оплата пошлины", "опять");

		//FinancialOperation actual = service.createOrUpdate();
//
//		assertNotNull(actual);
//		assertNotNull(actual.getCategory());
//		assertEquals(expected.getDescription(), actual.getDescription());
	}

	@Test
	void findAllForUser() {

	}

	@Test
	void deleteAllForFamilyMember() {

	}

	@Test
	void findAllForUserBetweenDates() {

	}

	@Test
	void findAllForFamilyMemberBetweenDates() {

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
		FinancialOperation expected0 = new FinancialOperation(new Date(), "расход",
				category, 999.99, "RUB", 1234567890L,
				"оплата пошлины", "опять");

		FinancialOperation expected = service.create(mapper.toFinancialOperationDto(expected0));
		FinancialOperation actual = service.findById(expected.getId());
		assertNotNull(actual);
		assertNotNull(actual.getCategory());
		assertEquals(expected.getDescription(), actual.getDescription());
	}
}
