package ru.nik.alfafamily.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FinancialOperation;
import ru.nik.alfafamily.exceptions.FamilyMemberDoesNotExistsException;
import ru.nik.alfafamily.repository.FinancialOperationRepository;
import ru.nik.alfafamily.util.Utilities;

@Service
@Slf4j
public class FinancialOperationServiceImpl implements FinancialOperationService {

	private final FinancialOperationRepository repository;

	private final FamilyMemberService familyMemberService;

	private final UserService userService;

	@Autowired
	public FinancialOperationServiceImpl(FinancialOperationRepository repository,
		FamilyMemberService familyMemberService, UserService userService) {
		this.repository = repository;
		this.familyMemberService = familyMemberService;
		this.userService = userService;
	}

	@Override
	public List<FinancialOperation> createOrUpdate(String userId, String familyMemberId,
		MultipartFile file) {
		if (!familyMemberService.isFamilyMemberExists(familyMemberId)) {
			throw new FamilyMemberDoesNotExistsException(
				"Family member with id " + familyMemberId + " doesn't exists.");
		}

		log.info("Parsing file: {} ..", file.getOriginalFilename());
		try {
			List<FinancialOperation> operations = Utilities.parseCsv(file);
			log.info("Parsing complete.");
			if (operations.size() > 0) {

				Map<String, Category> categoryMap = new HashMap<>();
				List<String> categoryList = new ArrayList<>();

				operations.forEach(o -> categoryMap.put(o.getCategory().getName(),
					new Category(o.getCategory().getName(), null)));

				categoryMap.values().forEach(category -> categoryList.add(category.getName()));

				FamilyMember member = familyMemberService
					.updateCategories(userId, familyMemberId, categoryList);

				operations.forEach(operation -> member.getCategories().forEach(m -> {
					if (operation.getCategory().getName().equals(m.getName())) {
						operation.setCategory(m);
					}
				}));
				return repository.saveAll(operations);
			}
			// TODO разобраться с  ексепшнами
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	@Override
	public List<FinancialOperation> findAllForUser(String userId) {
		if (userService.isUserExists(userId)) {
			List<FamilyMember> members = familyMemberService.findAll(userId);
			List<Category> allCategories = new ArrayList<>();
			members.forEach(m -> allCategories.addAll( m.getCategories()));
			return repository.findAllByCategoryIn(allCategories);
		}
		return Collections.emptyList();
	}

	@Override
	public Boolean deleteAllForFamilyMember(String userId, String familyMemberId) {
		if (userService.isUserExists(userId)) {
			List<FamilyMember> members = familyMemberService.findAll(userId);
			List<Category> allCategories = new ArrayList<>();
			members.forEach(m -> allCategories.addAll(m.getCategories()));
			return repository.deleteAllByCategoryIn(allCategories) != 0;
		}
		return false;
	}
}
