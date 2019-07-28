package ru.nik.alfafamily.service;

import java.util.Date;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.dto.FinancialOperationDto;

public interface FinancialOperationService {

	List<FinancialOperation> createOrUpdate(String userId, String familyMemberId, MultipartFile file);

	List<FinancialOperation> findAllForUser(String userId);

	Boolean deleteAllForFamilyMember(String userId, String familyMemberId);

	List<FinancialOperation> findAllForUserBetweenDates(String userId, Date start, Date end);

	List<FinancialOperation> findAllForFamilyMemberBetweenDates(String userId, String familyMemberId, Date start, Date end);

	FinancialOperation create(FinancialOperationDto dto);

	FinancialOperation update(FinancialOperationDto dto);

	Boolean delete(String operationId);

	FinancialOperation findById(String operationId);
}
