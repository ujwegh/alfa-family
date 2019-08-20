package ru.nik.alfafamily.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.nik.alfafamily.domain.Role;

@ExtendWith(SpringExtension.class)
@DataMongoTest
class RoleRepositoryTest {

	@Autowired
	private RoleRepository repository;

	@Autowired
	private MongoTemplate template;


	@BeforeEach
	public void init() {
		Role role1 = new Role("USER");
		Role role2 = new Role("ADMIN");
		Role role3 = new Role("TEST");

		template.save(role1);
		template.save(role2);
		template.save(role3);
	}

	@AfterEach
	public void cleanup() {
		template.getDb().drop();
	}

	@Test
	void findByName() {
		Role role = repository.findByName("ADMIN");
		assertNotNull(role);
		assertEquals("ADMIN", role.getName());
	}

	@Test
	void findAllByNameIn() {
		List<Role> roles = repository.findAllByNameIn(Arrays.asList("ADMIN", "TEST"));
		assertNotNull(roles);
		assertEquals(2, roles.size());
		assertEquals("ADMIN", roles.get(0).getName());
		assertEquals("TEST", roles.get(1).getName());
	}
}