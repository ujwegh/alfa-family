package ru.nik.alfafamily.controller.shell;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.multipart.MultipartFile;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.dto.FinancialOperationDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.service.CategoryService;
import ru.nik.alfafamily.service.FinancialOperationService;
import ru.nik.alfafamily.util.Utilities;

@Slf4j
@ShellComponent
public class ShellFinancialOperationController {

	private final FinancialOperationService service;

	private final CategoryService categoryService;

	private final Mapper mapper;

	@Autowired
	public ShellFinancialOperationController(FinancialOperationService service,
		CategoryService categoryService, Mapper mapper) {
		this.service = service;
		this.categoryService = categoryService;
		this.mapper = mapper;
	}

	@ShellMethod("Create financial operations from csv file")
	public String csvoperation(@ShellOption String userId, @ShellOption String memberId,
		@ShellOption String pathToFile) {

		File file = new File(pathToFile);

		MultipartFile result = null;
		try {
			result = Utilities.convertToMultipartFile(file);
		} catch (IOException e) {
		}

		List<FinancialOperation> operationList = service.createOrUpdate(userId, memberId, result);
		List<FinancialOperationDto> dtos = toDtoList(operationList);

		List<String> strings = dtos.stream().map(dto -> dto.toString() + "\n")
			.collect(Collectors.toList());

		return "New financial operations: \n" + strings.toString();
	}


	@ShellMethod("Show all financial operations for user")
	public String allforuser(@ShellOption String userId) {

		List<FinancialOperation> operations = service.findAllForUser(userId);
		List<FinancialOperationDto> dtos = toDtoList(operations);

		List<String> strings = dtos.stream().map(dto -> dto.toString() + "\n")
			.collect(Collectors.toList());

		return "All operations for user: " + userId + "\n" + strings;
	}

	@ShellMethod("Show all financial operations for user between dates")
	public String allforuserbetween(@ShellOption String userId, @ShellOption String startDate,
		@ShellOption String endDate) {

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		Date start;
		Date end;
		try {
			start = format.parse(startDate);
			end = format.parse(endDate);
		} catch (ParseException e) {
			return "Wrong startDate or endDate input format.";
		}

		List<FinancialOperation> operations = service
			.findAllForUserBetweenDates(userId, start, end);
		List<FinancialOperationDto> dtos = toDtoList(operations);

		List<String> strings = dtos.stream().map(dto -> dto.toString() + "\n")
			.collect(Collectors.toList());

		return "All operations for user: " + userId + ", between " + startDate + " end " + endDate
			+ "\n" + strings;
	}

	@ShellMethod("Show all financial operations for family member between dates")
	public String allformemberbetween(@ShellOption String userId,
		@ShellOption String familyMemberId,
		@ShellOption String startDate, @ShellOption String endDate) {

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		Date start;
		Date end;
		try {
			start = format.parse(startDate);
			end = format.parse(endDate);
		} catch (ParseException e) {
			return "Wrong startDate or endDate input format.";
		}

		List<FinancialOperation> operations = service
			.findAllForFamilyMemberBetweenDates(userId, familyMemberId, start, end);
		List<FinancialOperationDto> dtos = toDtoList(operations);

		List<String> strings = dtos.stream().map(dto -> dto.toString() + "\n")
			.collect(Collectors.toList());

		return "All operations for user: " + userId + ", between " + startDate + " end " + endDate
			+ "\n" + strings;
	}


	@ShellMethod("Create new financial operation")
	public String newoperation(@ShellOption String categoryId, @ShellOption String date,
		@ShellOption String type, @ShellOption String sum, @ShellOption String currency,
		@ShellOption String accountNumber) {

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		Date start;
		try {
			start = format.parse(date);
		} catch (ParseException e) {
			return "Wrong startDate or endDate input format.";
		}
		FinancialOperationDto dto = new FinancialOperationDto();
		dto.setDate(start);
		dto.setType(type);
		dto.setCategory(mapper.toCategoryDto(categoryService.findById(categoryId)));
		dto.setSum(Double.valueOf(sum));
		dto.setCurrency(currency);
		dto.setAccountNumber(Long.valueOf(accountNumber));
		dto.setPlanned(true);
		dto.setDescription("");
		dto.setComment("");

		// TODO сделать ввод description и comment для dto
		FinancialOperationDto operation = mapper.toFinancialOperationDto(service.create(dto));

		return "Created new financial operation: \n" + operation.toString();
	}

	@ShellMethod("Delete operation")
	public String deleteoperation(@ShellOption String operationId) {
		boolean b = service.delete(operationId);
		return b ? "Operation deleted." : "Delete operation failed.";
	}

	@ShellMethod("Find operation")
	public String findoperation(@ShellOption String operationId) {
		FinancialOperationDto dto = mapper.toFinancialOperationDto(service.findById(operationId));
		return dto.toString();
	}

	@ShellMethod("Delete all operations for family member")
	public String deletememberops(@ShellOption String userId, @ShellOption String familyMemberId) {
		boolean b = service.deleteAllForFamilyMember(userId, familyMemberId);
		return b ? "Operations deleted." : "Delete operations failed.";
	}

	public String updateoperation(@ShellOption String operationId, @ShellOption String categoryId,
		@ShellOption String date, @ShellOption String type, @ShellOption String sum,
		@ShellOption String currency, @ShellOption String accountNumber) {

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		Date start;
		try {
			start = format.parse(date);
		} catch (ParseException e) {
			return "Wrong startDate or endDate format.";
		}
		FinancialOperationDto dto = new FinancialOperationDto();
		dto.setDate(start);
		dto.setType(type);
		dto.setCategory(mapper.toCategoryDto(categoryService.findById(categoryId)));
		dto.setSum(Double.valueOf(sum));
		dto.setCurrency(currency);
		dto.setAccountNumber(Long.valueOf(accountNumber));
		dto.setPlanned(true);
		dto.setDescription("");
		dto.setComment("");

		// TODO сделать ввод description и comment для dto
		FinancialOperationDto operation = mapper.toFinancialOperationDto(service.create(dto));

		return "Updated financial operation: \n" + operation.toString();

	}


	private List<FinancialOperationDto> toDtoList(List<FinancialOperation> operationList) {
		return operationList.stream().map(mapper::toFinancialOperationDto)
			.collect(Collectors.toList());
	}


}
