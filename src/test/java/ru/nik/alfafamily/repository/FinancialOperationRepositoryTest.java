package ru.nik.alfafamily.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Date;
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
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;

@ExtendWith(SpringExtension.class)
@DataMongoTest
class FinancialOperationRepositoryTest {


	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private FinancialOperationRepository repository;

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
		template.save(member1);
		Category category1 = new Category("бензин", member1);
		template.save(category1);
		FinancialOperation operation = new FinancialOperation(new Date(), "расход", category1,
			234.0, "RUB", 1234567890L,
			"газ", "");
		template.save(operation);
	}

	@AfterEach
	public void cleanup() {
		template.getDb().drop();
	}

	@Test
	void save() {
		Category category = categoryRepository.findAll().get(0);
		FinancialOperation operation = new FinancialOperation(new Date(), "расход", category,
			555.55, "RUB", 1234567890L,
			"оплата бензина", "дороговато вышло");

		repository.save(operation);
		List<FinancialOperation> operations = repository.findAll();
		assertEquals(2, operations.size());
	}

	@Test
	void findAllByCategoryIn() {
		Category category = categoryRepository.findAll().get(0);
		List<FinancialOperation> operations = repository
			.findAllByCategoryInOrderByDateDesc(Collections.singletonList(category));
		assertEquals(1, operations.size());
	}

	@Test
	void deleteAllByCategory_Member_Id() {
		Category category = categoryRepository.findAll().get(0);
		int i = repository.deleteAllByCategoryIn(Collections.singletonList(category));
		assertEquals(1, i);
	}
}