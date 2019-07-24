package ru.nik.alfafamily.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.Role;
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
	void existsById() {
		User user = userRepository.findAll().get(0);
		boolean exist = userRepository.existsById(user.getId());
		assertTrue(exist);
	}
}