package ru.nik.alfafamily.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
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
class FamilyMemberRepositoryTest {


	@Autowired
	private FamilyMemberRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MongoTemplate template;


	@BeforeEach
	public void init() {
		User user = new User("firstName", "secondName",
			"admin@mail.com", "password");
		Role role = new Role("USER");
		template.save(role);
		user.setRoles(Collections.singleton(role));
		template.save(user);

		FamilyMember member1 = new FamilyMember("test-1-member", user);
		FamilyMember member2 = new FamilyMember("test-2-member", user);
		template.save(member1);
		template.save(member2);
	}


	@Test
	void findAllByUser_Id() {
		User user = userRepository.findByEmail("admin@mail.com");
		List<FamilyMember> members = repository.findAllByUser_Id(user.getId());

		assertNotNull(members);
		assertEquals(2, members.size());
	}

	@Test
	void findByUser_IdAndId() {
		User user = userRepository.findByEmail("admin@mail.com");
		List<FamilyMember> members = repository.findAll();

		FamilyMember member = repository.findByUser_IdAndId(user.getId(), members.get(0).getId());
		assertNotNull(member);
		assertEquals(members.get(0), member);
	}

	@Test
	void deleteByUser_IdAndId() {
		User user = userRepository.findByEmail("admin@mail.com");
		List<FamilyMember> members = repository.findAll();

		Long i = repository.deleteByUser_IdAndId(user.getId(), members.get(0).getId());
		assertNotNull(i);
		assertEquals(1, i.longValue());
		FamilyMember member = repository.findByUser_IdAndId(user.getId(), members.get(0).getId());
		assertNull(member);
		members = repository.findAll();
		assertEquals(1, members.size());
	}

	@Test
	void existsById() {
		User user = userRepository.findByEmail("admin@mail.com");
		List<FamilyMember> members = repository.findAll();

		assertTrue(repository.existsById(members.get(0).getId()));
	}
}