package ru.nik.alfafamily.controller;

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
import ru.nik.alfafamily.exceptions.ParseCsvException;
import ru.nik.alfafamily.service.BudgetService;

@RestController
public class BudgetController {

	private final BudgetService service;

	@Autowired
	public BudgetController(BudgetService service) {
		this.service = service;
	}

	@PostMapping(value = "/uploadBudgetFile")
	public List<FinancialOperationDto> updateFinOperationHistory(@RequestParam("budget") MultipartFile file) {
		List<FinancialOperation> operations = service.createOrUpdate("A", "a", file);
		if (operations == null)
			throw new ParseCsvException("Parsing csv has been failed");

		return operations.stream()
			.map(FinancialOperationMapper.INSTANCE::toFinancialOperationDto)
			.collect(Collectors.toList());
	}

}
