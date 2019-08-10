package ru.nik.alfafamily.controller.shell;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.nik.alfafamily.dto.FinancialOperationDto;
import ru.nik.alfafamily.service.BudgetService;

@Slf4j
@ShellComponent
public class ShellBudgetController {

	private final BudgetService service;

	@Autowired
	public ShellBudgetController(BudgetService service) {
		this.service = service;
	}

	@ShellMethod("Show budget for user")
	public List<FinancialOperationDto> budget(@ShellOption String email) {
		log.info("Show budget for user: " + email);

//		service.countForAllTimeForUser()

		return null;
	}
}
