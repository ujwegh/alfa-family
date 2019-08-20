package ru.nik.alfafamily.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.nik.alfafamily.domain.User;

@ExtendWith(SpringExtension.class)
@DataMongoTest
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MongoTemplate template;

	@BeforeEach
	public void init() {
		User user = new User("firstName", "secondName",
				"admin@mail.com", "password");
		template.save(user);
	}

	@AfterEach
	public void cleanup() {
		template.getDb().drop();
	}

	@Test
	void findByEmail() {
		User user = userRepository.findByEmail("admin@mail.com");
		assertNotNull(user);
		assertEquals("admin@mail.com", user.getEmail());
	}
	@Test
	void findById() {
		User user0 = userRepository.findAll().get(0);
		User user = userRepository.findById(user0.getId()).orElse(null);
		assertNotNull(user);
		assertEquals(user0.getId(), user.getId());
	}
	@Test
	void findAllByIdIn() {
		List<User> users = userRepository.findAll();
		assertNotNull(users);
		assertEquals(1, users.size());
	}

	@Test
	void existsById() {
		User user = userRepository.findAll().get(0);
		boolean exist = userRepository.existsById(user.getId());
		assertTrue(exist);
	}

	@Test
	void existsByEmail() {
		User user = userRepository.findAll().get(0);
		boolean exist = userRepository.existsByEmail(user.getEmail());
		assertTrue(exist);
	}
}