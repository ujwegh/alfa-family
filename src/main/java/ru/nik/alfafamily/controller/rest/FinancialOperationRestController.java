package ru.nik.alfafamily.controller.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import ru.nik.alfafamily.dto.DateBetweenRequestDto;
import ru.nik.alfafamily.dto.FinancialOperationDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.service.FinancialOperationService;

@Api(value = "Financial operation rest controller", description = "Financial operations manager")
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

//	@PreAuthorize("#userId == authentication.details.id")
	@ApiOperation(value = "Find all financial operations for user", response = List.class)
	@GetMapping("/user/{userId}")
	public List<FinancialOperationDto> findAllForUser(@PathVariable @NonNull final String userId) {
		log.info("Find all financial operations for user: {}", userId);
		List<FinancialOperation> list = service.findAllForUser(userId);
		List<FinancialOperationDto> dtos = new ArrayList<>();
		list.forEach(operation -> dtos.add(mapper.toFinancialOperationDto(operation)));
		return dtos;
	}

	@ApiOperation(value = "Delete operation with id")
	@DeleteMapping("/operation/{operationId}")
	public void delete(@PathVariable String operationId) {
		log.info("Delete operation with id: {}", operationId);
		service.delete(operationId);
	}

	@ApiOperation(value = "Create new financial operation", response = FinancialOperationDto.class)
	@PostMapping("/operation")
	public FinancialOperationDto create(@RequestBody FinancialOperationDto operationDto) {
		log.info("Create new financial operation: {}", operationDto.toString());
		FinancialOperation operation = service.create(operationDto);
		return operation != null ? mapper.toFinancialOperationDto(operation) : null;
	}

	@ApiOperation(value = "Find financial operation by id", response = FinancialOperationDto.class)
	@GetMapping("/operation/{operationId}")
	public FinancialOperationDto findById(@PathVariable String operationId) {
		log.info("Find financial operation by id: {}", operationId);
		FinancialOperation operation = service.findById(operationId);
		return operation != null ? mapper.toFinancialOperationDto(operation) : null;
	}

	@ApiOperation(value = "Find financial operation by id", response = FinancialOperationDto.class)
	@PostMapping("/upload/{familyMemberId}")
	public List<FinancialOperationDto> createFromCsv(@PathVariable String familyMemberId,
		@RequestParam("file") MultipartFile file) {
		log.info("Create financial operations from csv file: {}", file.getName());
		List<FinancialOperation> list = service.createOrUpdate(familyMemberId, file);

		List<FinancialOperationDto> dtos = new ArrayList<>();
		list.forEach(operation -> dtos.add(mapper.toFinancialOperationDto(operation)));
		return dtos;
	}

	@PostMapping("/user/between/{userId}")
	public List<FinancialOperationDto> userOperationsBetween(@PathVariable String userid,
		@RequestBody DateBetweenRequestDto dto) {
		List<FinancialOperation> operations = service
			.findAllForUserBetweenDates(userid, dto.getStartDate(), dto.getEndDate());
		List<FinancialOperationDto> dtos = new ArrayList<>();

		operations.forEach(operation -> dtos.add(mapper.toFinancialOperationDto(operation)));
		return dtos;
	}

	@PostMapping("/member/between/{familyMemberId}")
	public List<FinancialOperationDto> memberOperationsBetween(@PathVariable String familyMemberId,
		@RequestBody DateBetweenRequestDto dto) {
		List<FinancialOperation> operations = service
			.findAllForUserBetweenDates(familyMemberId, dto.getStartDate(), dto.getEndDate());
		List<FinancialOperationDto> dtos = new ArrayList<>();

		operations.forEach(operation -> dtos.add(mapper.toFinancialOperationDto(operation)));
		return dtos;
	}


}
