package ru.nik.alfafamily.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.dto.UserDto;
import ru.nik.alfafamily.dto.UserRegistrationDto;
import ru.nik.alfafamily.repository.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@EnableMongoRepositories(basePackages = {"ru.nik.alfafamily.repository"})
@EnableAutoConfiguration
@ContextConfiguration(classes = {CategoryServiceImpl.class, FamilyMemberServiceImpl.class,
	UserServiceImpl.class, Mapper.class, FamilyMemberPropertiesServiceImpl.class,
	BCryptPasswordEncoder.class})
class UserServiceImplTest {

	@Autowired
	private UserRepository repository;

	@Autowired
	private MongoTemplate template;

	@Autowired
	private UserService userService;

	@Autowired
	private Mapper mapper;

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
	void save() {
		UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
		userRegistrationDto.setEmail("user@mail.ru");
		userRegistrationDto.setFirstName("usr1");
		userRegistrationDto.setLastName("user1");
		userRegistrationDto.setPassword("11user");
		User actual = userService.save(userRegistrationDto);
		assertNotNull(actual);
		assertEquals("user@mail.ru", actual.getEmail());
		assertEquals("usr1", actual.getFirstName());
		assertEquals("user1", actual.getLastName());
	}

	@Test
	void update() {
		User user = repository.findAll().get(0);
		UserDto expected = mapper.toUserDto(user);
		expected.setLastName("halo");
		expected.setFirstName("halo");
		User actual = userService.update(expected);
		assertNotNull(actual);
		assertNotNull(actual.getEmail());
		assertNotNull(actual.getId());
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getEmail(), actual.getEmail());
		assertEquals(expected.getFirstName(), actual.getFirstName());
	}

	@Test
	void findByEmail() {
		User expected = repository.findAll().get(0);
		User actual = userService.findByEmail(expected.getEmail());
		assertNotNull(actual);
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getEmail(), actual.getEmail());
		assertEquals(expected.getFirstName(), actual.getFirstName());
	}

	@Test
	void findById() {
		User expected = repository.findAll().get(0);
		User actual = userService.findById(expected.getId());
		assertNotNull(actual);
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getEmail(), actual.getEmail());
		assertEquals(expected.getFirstName(), actual.getFirstName());
	}

	@Test
	void isUserExistsById() {
		User expected = repository.findAll().get(0);
		Boolean b = userService.isUserExistsById(expected.getId());
		assertTrue(b);
	}

	@Test
	void isUserExistsByEmail() {
		User user = repository.findAll().get(0);
		Boolean b = userService.isUserExistsByEmail(user.getEmail());
		assertTrue(b);
	}

	@Test
	void findAll() {
		User expected = repository.findAll().get(0);
		List<User> actual = userService.findAll();
		actual.forEach(u -> assertEquals(expected.getId(), u.getId()));
		assertTrue(actual.size() > 0);
	}

	@Test
	void findAllByIdIn() {
		User user = repository.findAll().get(0);
		List<String> list = new ArrayList<>();
		list.add(user.getId());
		List<User> users = userService.findAllByIdIn(list);
		users.forEach(u -> assertEquals(user.getId(), u.getId()));
		assertEquals(1,list.size());
	}
}