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
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;

@ExtendWith(SpringExtension.class)
@DataMongoTest
class CategoryRepositoryTest {

	@Autowired
	private CategoryRepository categoryRepository;

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
	void save() {
		FamilyMember member = memberRepository.findAll().get(0);
		Category category = new Category("кино", member);

		Category actual = categoryRepository.save(category);

		assertNotNull(actual);
		assertEquals(category.getName(), actual.getName());
	}

	@Test
	void findByMember_IdAndName() {
		FamilyMember member = memberRepository.findAll().get(0);
		Category category = categoryRepository.findByFamilyMember_IdAndName(member.getId(), "бензин");
		assertNotNull(category);
		assertEquals("бензин", category.getName());
		assertEquals(member.getId(), category.getFamilyMember().getId());
	}

	@Test
	void findAllByMember_Id() {
		FamilyMember member = memberRepository.findAll().get(0);

		Category category1 = new Category("бензин", member);
		Category category2 = new Category("продукты", member);
		List<Category> categories = categoryRepository.findAllByFamilyMember_Id(member.getId());
		assertEquals(2, categories.size());
		assertEquals(category1.getName(), categories.get(0).getName());
		assertEquals(category2.getName(), categories.get(1).getName());
	}

	@Test
	void findAllByMember_IdAndNameIn() {
		FamilyMember member = memberRepository.findAll().get(0);
		Category category1 = new Category("бензин", member);
		List<Category> categories = categoryRepository.findAllByFamilyMember_IdAndNameIn(member.getId(),
				Collections.singletonList(category1.getName()));

		assertEquals(1, categories.size());
		assertEquals(category1.getName(), categories.get(0).getName());
	}

	@Test
	void deleteByMember_IdAndName() {
		FamilyMember member = memberRepository.findAll().get(0);
		Category category1 = new Category("бензин", member);
		Category category2 = new Category("продукты", member);
		Long i = categoryRepository.deleteByFamilyMember_IdAndName(member.getId(), category1.getName());
		assertEquals(1, i.longValue());
		assertEquals(1, categoryRepository.findAll().size());
	}

	@Test
	void existsByMember_IdAndName() {
		FamilyMember member = memberRepository.findAll().get(0);
		Category category1 = new Category("бензин", member);
		boolean exist = categoryRepository.existsByFamilyMember_IdAndName(member.getId(), category1.getName());
		assertTrue(exist);
	}
}