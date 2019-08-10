package ru.nik.alfafamily.controller.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.multipart.MultipartFile;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.dto.FinancialOperationDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.service.FinancialOperationService;
import org.springframework.mock.web.MockMultipartFile;
import ru.nik.alfafamily.util.Utilities;

@Slf4j
@ShellComponent
public class ShellFinancialOperationController {

	private final FinancialOperationService service;

	private final Mapper mapper;

	@Autowired
	public ShellFinancialOperationController(FinancialOperationService service,
		Mapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@ShellMethod("Update financial operations by csv file")
	public String updateOperations(String userId, String memberId, String pathToFile) {

		File file = new File(pathToFile);

		MultipartFile result = null;
		try {
			result = Utilities.convertToMultipartFile(file);
		} catch (IOException e) {
		}

		List<FinancialOperation> operationList = service.createOrUpdate(userId, memberId, result);
		List<FinancialOperationDto> dtos = operationList.stream()
			.map(mapper::toFinancialOperationDto).collect(
				Collectors.toList());

		List<String> strings = dtos.stream().map(dto -> dto.toString() + "\n").collect(Collectors.toList());

		return "New financial operations: \n" + strings.toString();
	}


}
