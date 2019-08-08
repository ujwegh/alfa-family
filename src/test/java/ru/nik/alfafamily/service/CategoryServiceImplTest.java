package ru.nik.alfafamily.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.junit4.SpringRunner;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
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
	UserServiceImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
		user.setRoles(Collections.singleton(role));
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

	}

	@Test
	void bulkCreate() {

	}

	@Test
	void update() {


	}

	@Test
	void delete() {

	}

	@Test
	void findAll() {

	}

	@Test
	void findAll1() {

	}

	@Test
	void get() {

	}
}