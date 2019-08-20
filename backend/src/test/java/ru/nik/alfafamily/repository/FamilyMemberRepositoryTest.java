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
		user.setRole(role);
		template.save(user);

		FamilyMember member1 = new FamilyMember("test-1-familyMember", user);
		FamilyMember member2 = new FamilyMember("test-2-familyMember", user);
		template.save(member1);
		template.save(member2);
	}

	@AfterEach
	public void cleanup() {
		template.getDb().drop();
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
		List<FamilyMember> members = repository.findAll();

		FamilyMember member = repository.findById(members.get(0).getId()).orElse(null);
		assertNotNull(member);
		assertEquals(members.get(0).getId(), member.getId());
		assertEquals(members.get(0).getName(), member.getName());
		assertEquals(members.get(0).getUser().getId(), member.getUser().getId());
	}

	@Test
	void deleteByUser_IdAndId() {
		List<FamilyMember> members = repository.findAll();

		repository.deleteById(members.get(0).getId());
		FamilyMember member = repository.findById(members.get(0).getId()).orElse(null);
		assertNull(member);
		members = repository.findAll();
		assertEquals(1, members.size());
	}

	@Test
	void existsById() {
		List<FamilyMember> members = repository.findAll();

		assertTrue(repository.existsById(members.get(0).getId()));
	}
}