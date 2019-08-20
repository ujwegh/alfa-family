package ru.nik.alfafamily.controller.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
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
@RequestMapping("/rest/{userId}/finoperations")
public class FinancialOperationRestController {

	private final FinancialOperationService service;

	private final Mapper mapper;

	@Autowired
	public FinancialOperationRestController(
		FinancialOperationService service, Mapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Find all financial operations for user", response = List.class)
	@GetMapping
	public List<FinancialOperationDto> findAllForUser(@PathVariable @Nonnull final String userId) {
		log.info("Find all financial operations for user: {}", userId);
		List<FinancialOperation> list = service.findAllForUser(userId);
		List<FinancialOperationDto> dtos = new ArrayList<>();
		list.forEach(operation -> dtos.add(mapper.toFinancialOperationDto(operation)));
		return dtos;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Delete operation with id")
	@DeleteMapping("/{operationId}")
	public void delete(@PathVariable @Nonnull final String userId, @PathVariable String operationId) {
		log.info("Delete operation with id: {}", operationId);
		service.delete(operationId);
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Create new financial operation", response = FinancialOperationDto.class)
	@PostMapping
	public FinancialOperationDto create(@PathVariable @Nonnull final String userId,
		@RequestBody FinancialOperationDto operationDto) {
		log.info("Create new financial operation: {}", operationDto.toString());
		FinancialOperation operation = service.create(operationDto);
		return operation != null ? mapper.toFinancialOperationDto(operation) : null;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Find financial operation by id", response = FinancialOperationDto.class)
	@GetMapping("/{operationId}")
	public FinancialOperationDto findById(@PathVariable @Nonnull final String userId,
		@PathVariable String operationId) {
		log.info("Find financial operation by id: {}", operationId);
		FinancialOperation operation = service.findById(operationId);
		return operation != null ? mapper.toFinancialOperationDto(operation) : null;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Find financial operation by id", response = FinancialOperationDto.class)
	@PostMapping("/upload/{familyMemberId}")
	public List<FinancialOperationDto> createFromCsv(@PathVariable @Nonnull final String userId,
		@PathVariable String familyMemberId,
		@RequestParam("file") MultipartFile file) {
		log.info("Create financial operations from csv file: {}", file.getName());
		List<FinancialOperation> list = service.createOrUpdate(familyMemberId, file);

		List<FinancialOperationDto> dtos = new ArrayList<>();
		list.forEach(operation -> dtos.add(mapper.toFinancialOperationDto(operation)));
		return dtos;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@PostMapping("/user/between")
	public List<FinancialOperationDto> userOperationsBetween(@PathVariable @Nonnull final String userId,
		@RequestBody DateBetweenRequestDto dto) {
		List<FinancialOperation> operations = service
			.findAllForUserBetweenDates(userId, dto.getStartDate(), dto.getEndDate());
		List<FinancialOperationDto> dtos = new ArrayList<>();

		operations.forEach(operation -> dtos.add(mapper.toFinancialOperationDto(operation)));
		return dtos;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@PostMapping("/member/{familyMemberId}/between")
	public List<FinancialOperationDto> memberOperationsBetween(@PathVariable @Nonnull final String userId,
		@PathVariable String familyMemberId, @RequestBody DateBetweenRequestDto dto) {
		List<FinancialOperation> operations = service
			.findAllForUserBetweenDates(familyMemberId, dto.getStartDate(), dto.getEndDate());
		List<FinancialOperationDto> dtos = new ArrayList<>();

		operations.forEach(operation -> dtos.add(mapper.toFinancialOperationDto(operation)));
		return dtos;
	}

}
