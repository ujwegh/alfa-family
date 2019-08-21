package ru.nik.alfafamily.service;

import static org.junit.jupiter.api.Assertions.*;

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
import ru.nik.alfafamily.domain.*;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.repository.FamilyMemberRepository;
import ru.nik.alfafamily.repository.UserRepository;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@EnableMongoRepositories(basePackages = {"ru.nik.alfafamily.repository"})
@EnableAutoConfiguration
@ContextConfiguration(classes = {CategoryServiceImpl.class, FamilyMemberServiceImpl.class,
	UserServiceImpl.class, Mapper.class, FamilyMemberPropertiesServiceImpl.class, RoleServiceImpl.class})
class FamilyMemberServiceImplTest {

	@Autowired
	private MongoTemplate template;

	@Autowired
	private FamilyMemberService familyMemberService;

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
		FamilyMemberProperties familyMemberProperties = new FamilyMemberProperties(member1,
			"green");
		template.save(familyMemberProperties);

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
	void findAll() {
		List<FamilyMember> list = familyMemberService
			.findAll(userRepository.findAll().get(0).getId());
		assertNotNull(list);
		assertNotNull(list.get(0).getUser());
		assertEquals(1, list.size());
	}

	@Test
	void create() {
		User user = userRepository.findAll().get(0);
		FamilyMember familyMember1 = familyMemberService.create(user.getId(), "Mama");
		assertNotNull(familyMember1);
		assertNotNull(familyMember1.getUser());
		assertNotNull(familyMember1.getId());
		assertNotNull(familyMember1.getName());
		assertEquals("Mama", familyMember1.getName());
		assertEquals(user.getId(), familyMember1.getUser().getId());
	}

	@Test
	void update() {
		FamilyMember familyMember = memberRepository.findAll().get(0);
		FamilyMember familyMember1 = familyMemberService.update(familyMember.getId(), "Mama");
		assertNotNull(familyMember1);
		assertNotNull(familyMember1.getId());
		assertNotNull(familyMember1.getUser());
		assertNotNull(familyMember1.getName());
		assertEquals("Mama", familyMember1.getName());
		assertEquals(familyMember1.getId(), familyMember.getId());
	}

	@Test
	void delete() {
		FamilyMember familyMember = memberRepository.findAll().get(0);
		boolean b = familyMemberService.delete(familyMember.getId());
		assertTrue(b);
	}

	@Test
	void findById() {
		FamilyMember familyMember = memberRepository.findAll().get(0);
		FamilyMember familyMember1 = familyMemberService.findById(familyMember.getId());
		assertNotNull(familyMember1);
		assertNotNull(familyMember1.getId());
		assertNotNull(familyMember1.getUser());
		assertNotNull(familyMember1.getName());
		assertEquals(familyMember.getName(), familyMember1.getName());
		assertEquals(familyMember1.getId(), familyMember.getId());
	}

	@Test
	void isFamilyMemberExists() {
		FamilyMember familyMember = memberRepository.findAll().get(0);
		boolean b = familyMemberService.isFamilyMemberExists(familyMember.getId());
		assertTrue(b);
	}
}