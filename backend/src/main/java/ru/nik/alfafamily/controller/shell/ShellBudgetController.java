package ru.nik.alfafamily.controller.shell;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.nik.alfafamily.domain.Budget;
import ru.nik.alfafamily.service.BudgetService;

@Slf4j
@ShellComponent
public class ShellBudgetController {

	private final BudgetService service;

	@Autowired
	public ShellBudgetController(BudgetService service) {
		this.service = service;
	}

	@ShellMethod("Show budget for user for all time")
	public String user_budget(@ShellOption String userId) {
		Budget budget = service.countForAllTimeForUser(userId);
		if (budget == null) return "Cant calculate budget for user: " + userId;
		return budget.toString();
	}

	@ShellMethod("Show budget for family member for all time")
	public String family_member_budget(@ShellOption String userId, @ShellOption String familyMemberId) {
		Budget budget = service.countForAllTimeForFamilyMember(userId, familyMemberId);
		if (budget == null) return "Cant calculate budget for family member: " + familyMemberId;
		return budget.toString();
	}

	@ShellMethod("Show budget for user between dates")
	public String user_budget_between(@ShellOption String userId, @ShellOption String startDate,
		@ShellOption String endDate) {

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date start;
		Date end;
		try {
			start = format.parse(startDate);
			end = format.parse(endDate);
		} catch (ParseException e) {
			return "Wrong startDate or endDate input format.";
		}
		Budget budget = service.countForUserBetweenDates(userId, start, end);
		if (budget == null) return "Cant calculate budget for user: " + userId;
		return budget.toString();
	}

	@ShellMethod("Show budget for family member between dates")
	public String family_member_budget_between(@ShellOption String userId, @ShellOption String familyMemberId,
		@ShellOption String startDate, @ShellOption String endDate) {

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date start;
		Date end;
		try {
			start = format.parse(startDate);
			end = format.parse(endDate);
		} catch (ParseException e) {
			return "Wrong startDate or endDate input format.";
		}
		Budget budget = service.countForFamilyMemberBetweenDates(userId, familyMemberId, start, end);
		if (budget == null) return "Cant calculate budget for user: " + userId;
		return budget.toString();
	}
}
