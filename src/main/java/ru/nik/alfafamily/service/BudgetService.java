package ru.nik.alfafamily.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import ru.nik.alfafamily.domain.FinancialOperation;

public interface BudgetService {

	List<FinancialOperation> createOrUpdate(String userId, String familyMemberId, MultipartFile file);

	List<FinancialOperation> findAllForUser(String userId);

	Boolean cleanAllForFamilyMember(String userId, String familyMemberId);

}
