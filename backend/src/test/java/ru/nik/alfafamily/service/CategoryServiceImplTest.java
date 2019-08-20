package ru.nik.alfafamily.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
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
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.repository.CategoryRepository;
import ru.nik.alfafamily.repository.FamilyMemberRepository;
import ru.nik.alfafamily.repository.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@EnableMongoRepositories(basePackages = {"ru.nik.alfafamily.repository"})
@EnableAutoConfiguration
@ContextConfiguration(classes = {CategoryServiceImpl.class, FamilyMemberServiceImpl.class,
	UserServiceImpl.class, Mapper.class, FamilyMemberPropertiesServiceImpl.class})
class CategoryServiceImplTest {

	@Autowired
	private CategoryService service;

	@Autowired
	private MongoTemplate template;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private FamilyMemberRepository memberRepository;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	public void init() {
		User user = new User("firstName", "secondName",
				"admin@mail.com", "password");
		Role role = new Role("USER");
		template.save(role);
		user.setRole(role);
		template.save(user);

		FamilyMember member1 = new FamilyMember("test-1-familyMember", user);
		template.save(member1);

		Category category1 = new Category("бензин", member1);
		Category category2 = new Category("продукты", member1);
		template.save(category1);
		template.save(category2);
	}

	@AfterEach
	public void cleanup() {
		template.getDb().drop();
	}

	@Test
	void create() {
		User user = userRepository.findAll().get(0);
		FamilyMember member = memberRepository.findAll().get(0);
		Category category = service.create(member.getId(), "Еда и рестораны");
		assertNotNull(category);
		assertNotNull(category.getFamilyMember());
		assertNotNull(category.getFamilyMember().getUser());
		assertEquals("Еда и рестораны" , category.getName());
		assertEquals(user.getId(), category.getFamilyMember().getUser().getId());
		assertEquals(member.getId(), category.getFamilyMember().getId());
	}

	@Test
	void bulkCreate() {
		List<Category> categories = categoryRepository.findAll();
		List<Category> categories1 = service.bulkCreate(categories);
		assertNotNull(categories1);
		assertEquals(categories.size(), categories1.size());
	}

	@Test
	void update() {
		Category category = categoryRepository.findAll().get(0);
		Category category1 = service.updateByName(category.getFamilyMember().getId(),category.getName(), "Развлечения");
		assertNotNull(category1);
		assertNotNull(category1.getFamilyMember());
		assertNotNull(category1.getFamilyMember().getUser());
		assertEquals("Развлечения" , category1.getName());
		assertEquals(category.getId(), category1.getId());
	}

	@Test
	void delete() {
		Category category = categoryRepository.findAll().get(0);
		Boolean b = service.deleteByName(category.getFamilyMember().getId(),category.getName());
		assertNotNull(b);
		assertTrue(b);
	}

	@Test
	void findAll() {

		Category category = categoryRepository.findAll().get(0);
		List<Category> categories1 = service.findAll(category.getFamilyMember().getId());
		for (Category c: categories1) {
			assertEquals(category.getFamilyMember().getId(), c.getFamilyMember().getId());
		}
		assertTrue(categories1.size() > 0);
	}

	@Test
	void findAllByNamesIn() {
		Category category = categoryRepository.findAll().get(0);
		List<String> names = new ArrayList<>();
		names.add("бензин");
		names.add("продукты");
		List<Category> categories = service.findAllByNamesIn(category.getFamilyMember().getId(),names);
		assertTrue(categories.size() > 0);
		assertEquals(2, categories.size());

	}

	@Test
	void findByName() {
		Category category = categoryRepository.findAll().get(0);
		Category category1 = service.findByName(category.getFamilyMember().getId(),category.getName());
		assertNotNull(category1);
		assertNotNull(category1.getFamilyMember());
		assertNotNull(category1.getFamilyMember().getUser());
		assertEquals(category.getFamilyMember().getId(),category1.getFamilyMember().getId());
		assertEquals(category1.getId(),category.getId());

	}
	@Test
	void findById() {
		Category category = categoryRepository.findAll().get(0);
		Category category1 = service.findById(category.getId());
		assertNotNull(category1);
		assertNotNull(category1.getFamilyMember());
		assertNotNull(category1.getFamilyMember().getUser());
		assertEquals(category.getFamilyMember().getId(),category1.getFamilyMember().getId());
		assertEquals(category1.getId(),category.getId());
	}
}