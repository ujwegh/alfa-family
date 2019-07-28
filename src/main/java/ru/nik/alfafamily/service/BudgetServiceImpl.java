package ru.nik.alfafamily.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nik.alfafamily.domain.Budget;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.domain.FamilyMemberBudget;
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
		User user = userService.findById(userId);
		List<FinancialOperation> operations = service.findAllForUser(userId);
		Budget budget = Utilities.countBudget(operations);
		budget.setUser(user);
		return budget;
	}

	@Override
	public Budget countForAllTimeForFamilyMember(String userId, String memberId) {
		FamilyMember member = memberService.findById(userId, memberId);
		List<FinancialOperation> operations = service.findAllForUser(userId);
		List<FinancialOperation> memberOperations = operations.stream()
			.filter(o -> o.getCategory().getMember().getId().equals(memberId))
			.collect(Collectors.toList());
		FamilyMemberBudget budget = new FamilyMemberBudget(Utilities.countBudget(memberOperations));
		budget.setMember(member);
		return budget;
	}

	@Override
	public Budget countForUserBetweenDates(String userId, Date start, Date end) {
		User user = userService.findById(userId);
		List<FinancialOperation> operations = service.findAllForUserBetweenDates(userId, start, end);
		Budget budget = Utilities.countBudget(operations);
		budget.setUser(user);
		return budget;
	}

	@Override
	public Budget countForFamilyMemberBetweenDates(String userId, String memberId, Date start,
		Date end) {
		FamilyMember member = memberService.findById(userId, memberId);
		List<FinancialOperation> operations = service.findAllForFamilyMemberBetweenDates(userId, memberId, start, end);
		FamilyMemberBudget budget = new FamilyMemberBudget(Utilities.countBudget(operations));
		budget.setMember(member);
		return budget;
	}


}
