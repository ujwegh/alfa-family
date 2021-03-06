package ru.nik.alfafamily.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
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
import ru.nik.alfafamily.domain.FamilyMemberProperties;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;

@ExtendWith(SpringExtension.class)
@DataMongoTest
class FamilyMemberPropertiesRepositoryTest {


	@Autowired
	private FamilyMemberPropertiesRepository repository;

	@Autowired
	private FamilyMemberRepository memberRepository;

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
		template.save(member1);
		FamilyMemberProperties properties = new FamilyMemberProperties(member1, "c04000");
		template.save(properties);

	}

	@AfterEach
	public void cleanup() {
		template.getDb().drop();
	}

	@Test
	void deleteByFamilyMember_Id() {
		List<FamilyMember> members = memberRepository.findAll();
		int i = repository.deleteByFamilyMember_Id(members.get(0).getId());
		assertEquals(1, i);
		assertEquals(Collections.emptyList(), repository.findAll());
	}

	@Test
	void findByFamilyMember() {
		FamilyMember member1 = memberRepository.findAll().get(0);
		FamilyMemberProperties expected = new FamilyMemberProperties(member1, "c04000");
		FamilyMemberProperties actaul = repository.findByFamilyMember_Id(member1.getId());
		assertEquals(expected.getColor(), actaul.getColor());
	}
}