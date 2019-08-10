package ru.nik.alfafamily.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
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
class UserServiceImplTest {

	@Autowired
	private UserRepository repository;

	@Autowired
	private MongoTemplate template;

	@Autowired
	private FamilyMemberPropertiesService familyMemberPropertiesService;

	@Autowired
	private FamilyMemberRepository memberRepository;

	@Autowired
	private UserService userService;

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
	void save() {//    User save(UserRegistrationDto registration);
		User user = new User("firstName1", "secondName1",
			"admin@mail.com1", "password1");
		//
		// User user1 =

	}

	@Test
	void update() {//    User update(UserDto userDto);

	}

	@Test
	void findByEmail() {//    User findByEmail(String email);

	}

	@Test
	void findById() {//    User findById(String userId);

	}

	@Test
	void isUserExistsById() {//    Boolean isUserExistsById(String userId);

	}

	@Test
	void isUserExistsByEmail() {//    Boolean isUserExistsByEmail(String email);

	}

	@Test
	void findAll() {//    List<User> findAll();
//  Category category = categoryRepository.findAll().get(0);
//    List<Category> categories1 = service.findAll(category.getFamilyMember().getId());
//    List<Category> categories = new ArrayList<>();
//    for (Category c: categories1) {
//      assertEquals(category.getFamilyMember().getId(), c.getFamilyMember().getId());
//    }
//    assertTrue(categories1.size() > 0);
		User user = repository.findAll().get(0);
		List<User> users = userService.findAll();
		// List<User> users1 = new ArrayList<>();
		for (User u: users) {
			assertEquals(user.getId(), u.getId());
		}
		assertTrue(users.size()>0);
	}

	@Test
	void findAllByIdIn() {//    List<User> findAllByIdIn(List<String> ids);

	}
}