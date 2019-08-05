package ru.nik.alfafamily.controller.shell;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import ru.nik.alfafamily.service.FinancialOperationService;

@Slf4j
@ShellComponent
public class ShellFinancialOperationController {

	private final FinancialOperationService service;


	@Autowired
	public ShellFinancialOperationController(FinancialOperationService service) {
		this.service = service;
	}

	public String updateOperations(String path){
		return "dsf";
	}


















}
