package ru.nik.alfafamily.controller.shell;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.annotation.DirtiesContext;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.CategoryDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.dto.UserRegistrationDto;
import ru.nik.alfafamily.service.CategoryService;
import ru.nik.alfafamily.service.FamilyMemberService;
import ru.nik.alfafamily.service.UserService;

@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@EnableMongoRepositories(basePackages = {"ru.nik.alfafamily.repository"})
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShellCategoryControllerTest {

	@Autowired
	private CategoryService service;

	@Autowired
	private Mapper mapper;

	@Autowired
	private ShellCategoryController controller;

	@Autowired
	private FamilyMemberService memberService;

	@Autowired
	private UserService userService;

	@BeforeEach
	void init() {
		controller = new ShellCategoryController(service, mapper);

		UserRegistrationDto u = new UserRegistrationDto();
		u.setFirstName("firstName");
		u.setLastName("secondName");
		u.setEmail("admin@mail.com");
		u.setPassword("password");
		User user = userService.save(u);

		FamilyMember familyMember = memberService.create(user.getId(), "Mama");

		service.create(familyMember.getId(), "ЦАЦКИ");
	}

	@Test
	void category() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);
		Category category = service.findAll(familyMember.getId()).get(0);
		CategoryDto categoryDto = mapper.toCategoryDto(category);
		String s = controller.category(category.getId());
		assertNotNull(s);
		assertEquals("New category: " + categoryDto.toString(), s);
	}

	@Test
	void categories_by_names() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);

		List<String> namesOfCategories = new ArrayList<>();

		namesOfCategories.add("ЦАЦКИ");
		namesOfCategories.add("ПЕЦКИ");

		StringBuilder searchRequest = new StringBuilder();
		namesOfCategories.forEach(namesOfCategory ->
			searchRequest.append(namesOfCategory).append(","));
		searchRequest.substring(0, searchRequest.toString().length() - 1);

		List<Category> categories = service
			.findAllByNamesIn(familyMember.getId(), namesOfCategories);
		CategoryDto result = mapper.toCategoryDto(categories.get(0));

		String s = controller.categories_by_names(familyMember.getId(), searchRequest.toString());
		assertNotNull(s);
		assertEquals("Categories by names: \n" + Collections.singletonList(result), s);
	}

	@Test
	void categories() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);
		List<Category> categories = service.findAll(familyMember.getId());
		List<CategoryDto> categoriesDto = new ArrayList<>();
		for (Category c : categories
		) {
			categoriesDto.add(mapper.toCategoryDto(c));
		}
		String s = controller.categories(familyMember.getId());
		assertNotNull(s);
		assertEquals("Categories by names: \n" + categoriesDto.toString(), s);
	}

	@Test
	void new_category() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);
		String s = controller.new_category(familyMember.getId(), "Furniture");
		Category category = service.findByName(familyMember.getId(), "Furniture");
		CategoryDto categoryDto = mapper.toCategoryDto(category);
		assertNotNull(s);
		assertEquals("New category: " + categoryDto.toString(), s);
	}

	@Test
	void delete_category() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);
		String s = controller.delete_category(familyMember.getId(), "ЦАЦКИ");
		assertNotNull(s);
		assertEquals("Category deleted.", s);
	}

	@Test
	void category_by_name() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);
		Category category = service.findAll(familyMember.getId()).get(0);
		CategoryDto categoryDto = mapper.toCategoryDto(category);
		String s = controller.category_by_name(familyMember.getId(), "ЦАЦКИ");
		assertNotNull(s);
		assertEquals(categoryDto.toString(), s);
	}

	@Test
	void update_category() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);
		Category category = service.findAll(familyMember.getId()).get(0);
		CategoryDto categoryDto = mapper.toCategoryDto(category);
		String s = controller
			.update_category(familyMember.getId(), category.getName(), "Стройматериалы");
		categoryDto.setName("Стройматериалы");
		assertNotNull(s);
		assertEquals("Category updated:" + categoryDto.toString(), s);
	}
}