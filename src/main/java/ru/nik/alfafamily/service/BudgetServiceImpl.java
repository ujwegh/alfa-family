package ru.nik.alfafamily.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nik.alfafamily.domain.Budget;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.util.Utilities;

@Service
public class BudgetServiceImpl implements BudgetService {

	private final FinancialOperationService service;

	private final UserService userService;

	private final FamilyMemberService memberService;

	@Autowired
	public BudgetServiceImpl(FinancialOperationService service,
		UserService userService, FamilyMemberService memberService) {
		this.service = service;
		this.userService = userService;
		this.memberService = memberService;
	}

	@Override
	public Budget countForAllTimeForUser(String userId) {
		userService.isUserExistsById(userId);
		List<FinancialOperation> operations = service.findAllForUser(userId);
		Budget budget = Utilities.countBudget(operations);
		budget.setUserId(userId);
		return budget;
	}

	@Override
	public Budget countForAllTimeForFamilyMember(String userId, String memberId) {
		userService.isUserExistsById(userId);
		memberService.isFamilyMemberExists(memberId);
		List<FinancialOperation> operations = service.findAllForUser(userId);
		List<FinancialOperation> memberOperations = operations.stream()
			.filter(o -> o.getCategory().getFamilyMember().getId().equals(memberId))
			.collect(Collectors.toList());
		Budget budget = Utilities.countBudget(memberOperations);
		budget.setUserId(userId);
		budget.setFamilyMemberId(memberId);
		return budget;
	}

	@Override
	public Budget countForUserBetweenDates(String userId, Date start, Date end) {
		userService.isUserExistsById(userId);
		List<FinancialOperation> operations = service.findAllForUserBetweenDates(userId, start, end);
		Budget budget = Utilities.countBudget(operations);
		budget.setUserId(userId);
		return budget;
	}

	@Override
	public Budget countForFamilyMemberBetweenDates(String userId, String memberId, Date start, Date end) {
		userService.isUserExistsById(userId);
		memberService.isFamilyMemberExists(memberId);
		List<FinancialOperation> operations = service.findAllForFamilyMemberBetweenDates(userId, memberId, start, end);
		Budget budget = Utilities.countBudget(operations);
		budget.setUserId(userId);
		budget.setFamilyMemberId(memberId);
		return budget;
	}


}
