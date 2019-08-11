package ru.nik.alfafamily.controller.rest;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.dto.FinancialOperationDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.service.FinancialOperationService;

@Slf4j
@RestController
@RequestMapping("/rest/finoperations")
public class FinancialOperationRestController {

	private final FinancialOperationService service;

	private final Mapper mapper;

	@Autowired
	public FinancialOperationRestController(
		FinancialOperationService service, Mapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}


	@GetMapping("/user/{userId}")
	public List<FinancialOperationDto> findAllForUser(@PathVariable String userId) {
		log.info("Find all financial operations for user: {}", userId);
		List<FinancialOperation> list = service.findAllForUser(userId);
		List<FinancialOperationDto> dtos = new ArrayList<>();
		list.forEach(operation -> dtos.add(mapper.toFinancialOperationDto(operation)));
		return dtos;
	}

	@DeleteMapping("/operation/{operationId}")
	public void delete(@PathVariable String operationId) {
		log.info("Delete operation with id: {}", operationId);
		service.delete(operationId);
	}

	@PostMapping("/operation")
	public FinancialOperationDto create(@RequestBody FinancialOperationDto operationDto) {
		log.info("Create new financial operation: {}", operationDto.toString());
		FinancialOperation operation = service.create(operationDto);
		return operation != null ? mapper.toFinancialOperationDto(operation) : null;
	}

	@GetMapping("/operation/{operationId}")
	public FinancialOperationDto findById(@PathVariable String operationId) {
		FinancialOperation operation = service.findById(operationId);
		return operation != null ? mapper.toFinancialOperationDto(operation) : null;
	}

	@PostMapping("/upload/{familyMemberId}")
	public List<FinancialOperationDto> createFromCsv(@PathVariable String familyMemberId,
		@RequestParam("file") MultipartFile file) {
		List<FinancialOperation> list = service.createOrUpdate(familyMemberId, file);

		List<FinancialOperationDto> dtos = new ArrayList<>();
		list.forEach(operation -> dtos.add(mapper.toFinancialOperationDto(operation)));
		return dtos;
	}

//	public List<FinancialOperationDto>












}
