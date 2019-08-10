package ru.nik.alfafamily.controller.rest;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.dto.FinancialOperationDto;
import ru.nik.alfafamily.dto.FinancialOperationMapper;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.exceptions.ParseCsvException;
import ru.nik.alfafamily.service.FinancialOperationService;

@RestController
public class BudgetController {

	private final FinancialOperationService service;

	private final Mapper mapper;

	@Autowired
	public BudgetController(FinancialOperationService service, Mapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping(value = "/uploadBudgetFile")
	public List<FinancialOperationDto> updateFinOperationHistory(@RequestParam("budget") MultipartFile file) {
		List<FinancialOperation> operations = service.createOrUpdate("A", "a", file);
		if (operations == null)
			throw new ParseCsvException("Parsing csv has been failed");

		return operations.stream()
			.map(mapper::toFinancialOperationDto)
			.collect(Collectors.toList());
	}

}
