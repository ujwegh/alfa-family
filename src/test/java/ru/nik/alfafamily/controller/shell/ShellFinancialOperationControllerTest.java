package ru.nik.alfafamily.controller.shell;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.multipart.MultipartFile;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.FinancialOperationDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.dto.UserRegistrationDto;
import ru.nik.alfafamily.service.CategoryService;
import ru.nik.alfafamily.service.FamilyMemberService;
import ru.nik.alfafamily.service.FinancialOperationService;
import ru.nik.alfafamily.service.UserService;
import ru.nik.alfafamily.util.Utilities;

@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@EnableMongoRepositories(basePackages = {"ru.nik.alfafamily.repository"})
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShellFinancialOperationControllerTest {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private FinancialOperationService service;

	@Autowired
	private Mapper mapper;

	@Autowired
	private ShellFinancialOperationController controller;

	@Autowired
	private UserService userService;

	@Autowired
	private FamilyMemberService memberService;

	@BeforeEach
	void init() {
		controller = new ShellFinancialOperationController(service, categoryService, mapper);
		UserRegistrationDto regDto = new UserRegistrationDto();
		regDto.setFirstName("Firstname");
		regDto.setLastName("Lastname");
		regDto.setEmail("email");
		regDto.setConfirmEmail("email");
		regDto.setPassword("password");
		regDto.setConfirmPassword("password");
		regDto.setTerms(true);
		User user = userService.save(regDto);

		FamilyMember member = memberService.create(user.getId(), "Member");
		memberService.create(user.getId(), member.getName());
		Category category = categoryService.create(member.getId(), "some-category");

		FinancialOperation op1 = new FinancialOperation(new Date(), "расход",
			category, 555.55, "RUB", 1234567890L,
			"оплата продуктов", "дороговато вышло");
		FinancialOperation op2 = new FinancialOperation(new Date(), "расход",
			category, 999.99, "RUB", 1234567890L,
			"оплата пошлины", "опять");
		FinancialOperation op3 = new FinancialOperation(new Date(), "доход",
			category, 999.99, "RUB", 1234567890L,
			"возврат пошлины", "ура");
		FinancialOperation op4 = new FinancialOperation(new Date(), "доход",
			category, 1000.0, "RUB", 1234567890L,
			"возврат цены за торт", "ура!!!");

		service.create(mapper.toFinancialOperationDto(op1));
		service.create(mapper.toFinancialOperationDto(op2));
		service.create(mapper.toFinancialOperationDto(op3));
		service.create(mapper.toFinancialOperationDto(op4));
	}

	@Test
	void csv_operation() {
		User user = userService.findAll().get(0);
		List<FinancialOperation> existed = service.findAllForUser(user.getId());

		existed.forEach(e -> service.delete(e.getId())); // clean operations

		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);

		assertNotNull(familyMember);
		ClassLoader classLoader = getClass().getClassLoader();

		File file = new File(classLoader.getResource("Budget_2019-07-01-2019-07-31.csv").getFile());
		String s = controller.csv_operation(familyMember.getId(),file.getPath());
		assertNotNull(s);

		List<FinancialOperation> financialOperations = service.findAllForUser(user.getId());
		List<FinancialOperationDto> dtos = toDtoList(financialOperations);
		List<String> strings = dtos.stream().map(dto -> dto.toString() + "\n")
			.collect(Collectors.toList());

		assertEquals("New financial operations: \n" + strings.toString(), s);
	}

	@Test
	void all_forUser() {
		User user = userService.findAll().get(0);
		List<FinancialOperation> finOperations = service.findAllForUser(user.getId());
		List<FinancialOperationDto> dtos = toDtoList(finOperations);

		List<String> strings = dtos.stream().map(dto -> dto.toString() + "\n")
			.collect(Collectors.toList());

		String s = controller.all_for_user(user.getId());
		assertNotNull(s);
		assertEquals("All operations for user: " + user.getId() + "\n" + strings, s);
	}
	@Test
	void allForUserBetween() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);
		assertNotNull(familyMember);
		List<FinancialOperation> operations = service.findAllForUser(user.getId());


		Date start = operations.get(operations.size()-1).getDate();
		Date end = operations.get(0).getDate();

		List<FinancialOperation> finOperations = service.findAllForUserBetweenDates(user.getId(), start, end);
		List<FinancialOperationDto> dtos = toDtoList(finOperations);

		List<String> strings = dtos.stream().map(dto -> dto.toString() + "\n")
			.collect(Collectors.toList());

		String s = controller.all_for_user_between(user.getId(), parseDate(start), parseDate(end));
		assertNotNull(s);
		assertEquals("All operations for user: " + user.getId() + ", between " +
			parseDate(start) + " end " + parseDate(end) + "\n" + strings, s);
	}

	@Test
	void allForMemberBetween() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);
		assertNotNull(familyMember);
		List<FinancialOperation> operations = service.findAllForUser(user.getId());

		Date start = operations.get(operations.size()-1).getDate();
		Date end = operations.get(0).getDate();

		List<FinancialOperation> finOperations = service.findAllForFamilyMemberBetweenDates(user.getId(), familyMember.getId(), start, end);
		List<FinancialOperationDto> dtos = toDtoList(finOperations);

		List<String> strings = dtos.stream().map(dto -> dto.toString() + "\n")
			.collect(Collectors.toList());


		String s = controller.all_for_member_between(user.getId(), familyMember.getId(),
			parseDate(start), parseDate(end));
		assertNotNull(s);
		assertEquals("All operations for member: " + familyMember.getId() + ", between " +
			parseDate(start) + " end " + parseDate(end) + "\n" + strings, s);
	}

	@Test
	void newOperation() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);
		Category category = categoryService.findByName(familyMember.getId(), "some-category");
		List<FinancialOperation> operations = service.findAllForUser(user.getId());

		FinancialOperation op = new FinancialOperation(new Date(), "доход",
			category, 1000.0, "RUB", 1234567890L,
			"перевод долга", "наконец-то!");
		String s = controller.new_operation(category.getId(), parseDate(op.getDate()), op.getType(),
				op.getSum().toString(), op.getCurrency(), op.getAccountNumber().toString());

		FinancialOperation newOps = service.findAllForUser(user.getId()).get(operations.size());
		FinancialOperationDto dto = mapper.toFinancialOperationDto(newOps);
		assertNotNull(s);
		assertEquals("Created new financial operation: \n" + dto.toString(), s);
	}

	@Test
	void deleteOperation() {
		User user = userService.findAll().get(0);
		FinancialOperation finOperation = service.findAllForUser(user.getId()).get(0);
		assertNotNull(finOperation);
		String s = controller.delete_operation(finOperation.getId());
		assertNotNull(s);
		assertEquals("Operation deleted.", s);
	}

	@Test
	void findOperation() {
		User user = userService.findAll().get(0);
		FinancialOperation finOperation = service.findAllForUser(user.getId()).get(0);
		FinancialOperationDto dto = mapper.toFinancialOperationDto(finOperation);
		assertNotNull(dto);
		String s = controller.findo_peration(finOperation.getId());
		assertNotNull(s);
		assertEquals(dto.toString(), s);
	}

	@Test
	void deleteMemberOps() {
		User user = userService.findAll().get(0);
		FamilyMember familyMember = memberService.findAll(user.getId()).get(0);
		Date start = service.findAllForUser(user.getId()).get(0).getDate();
		Date end = service.findAllForUser(user.getId()).get(userService.findAll().size()).getDate();
		List<FinancialOperation> finOperations = service
			.findAllForFamilyMemberBetweenDates(user.getId(), familyMember.getId(), start, end);
		FinancialOperation operation1 = service.findById(finOperations.get(0).getId());
		FinancialOperationDto dto = mapper.toFinancialOperationDto(operation1);
		assertNotNull(dto);
		String s = controller.delete_memberops(user.getId(), familyMember.getId());
		assertNotNull(s);
		assertEquals("Operations deleted.", s);
	}

	@Test
	void updateOperation() {
		User user = userService.findAll().get(0);
		FinancialOperation finOperation = service.findAllForUser(user.getId()).get(0);

		String s = controller.update_operation(finOperation.getId(),
			finOperation.getCategory().getId(), parseDate(finOperation.getDate()),
			finOperation.getType(), "2500.0",
			finOperation.getCurrency(),
			String.valueOf(finOperation.getAccountNumber()));

		FinancialOperationDto updated = mapper.toFinancialOperationDto(service.findById(finOperation.getId()));

		assertNotNull(s);
		assertEquals("Updated financial operation: \n" + updated.toString(), s);
	}

	private List<FinancialOperationDto> toDtoList(List<FinancialOperation> operationList) {
		return operationList.stream().map(mapper::toFinancialOperationDto)
			.collect(Collectors.toList());
	}

	private String parseDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		return format.format(date);
	}

}
