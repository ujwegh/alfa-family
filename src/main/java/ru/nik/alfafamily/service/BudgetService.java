package ru.nik.alfafamily.service;

import java.util.Date;
import ru.nik.alfafamily.domain.Budget;

public interface BudgetService {

	Budget countForAllTimeForUser(String userId);

	Budget countForAllTimeForFamilyMember(String userId, String memberId);

	Budget countForUserBetweenDates(String userId, Date start, Date end);

	Budget countForFamilyMemberBetweenDates(String userId, String memberId, Date start, Date end);

}
