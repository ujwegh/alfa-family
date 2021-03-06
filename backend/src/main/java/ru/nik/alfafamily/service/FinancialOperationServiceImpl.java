package ru.nik.alfafamily.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import ru.nik.alfafamily.dto.FinancialOperationDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.exceptions.FamilyMemberDoesNotExistsException;
import ru.nik.alfafamily.exceptions.FinancialOperationException;
import ru.nik.alfafamily.exceptions.ParseCsvException;
import ru.nik.alfafamily.repository.FinancialOperationRepository;
import ru.nik.alfafamily.util.Utilities;

@Service
@Slf4j
public class FinancialOperationServiceImpl implements FinancialOperationService {

	private final FinancialOperationRepository repository;

	private final FamilyMemberService familyMemberService;

	private final UserService userService;

	private final Mapper mapper;

	private final CategoryService categoryService;

	@Autowired
	public FinancialOperationServiceImpl(FinancialOperationRepository repository,
		FamilyMemberService familyMemberService, UserService userService, Mapper mapper,
		CategoryService categoryService) {
		this.repository = repository;
		this.familyMemberService = familyMemberService;
		this.userService = userService;
		this.mapper = mapper;
		this.categoryService = categoryService;
	}

	@Override
	public List<FinancialOperation> createOrUpdate(String familyMemberId, MultipartFile file) {
		if (!familyMemberService.isFamilyMemberExists(familyMemberId)) {
			throw new FamilyMemberDoesNotExistsException(
				"Family familyMember with id " + familyMemberId + " doesn't exists.");
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

//				FamilyMember member = familyMemberService
//					.updateCategories(familyMemberId, categoryList);

				List<Category> categories = categoryService.bulkUpdate(familyMemberId, categoryList);

				operations.forEach(operation -> categories.forEach(m -> {
					if (operation.getCategory().getName().equals(m.getName())) {
						operation.setCategory(m);
					}
				}));

				return repository.saveAll(operations);
			}
		} catch (IOException | ParseException e) {
			throw new ParseCsvException("Parse csv file was failed.");
		}
		return Collections.emptyList();
	}

	@Override
	public List<FinancialOperation> findAllForUser(String userId) {
		return userService.isUserExistsById(userId) ? repository
			.findAllByCategoryInOrderByDateDesc(getUserCategories(userId))
			: Collections.emptyList();
	}

	@Override
	public Boolean deleteAllForFamilyMember(String userId, String familyMemberId) {
		if (userService.isUserExistsById(userId)) {
			return repository.deleteAllByCategoryIn(getFamilyMemberCategories(familyMemberId)) != 0;
		}
		return false;
	}

	@Override
	public List<FinancialOperation> findAllForUserBetweenDates(String userId, Date start,
		Date end) {
		if (userService.isUserExistsById(userId)) {

			List<Category> categories = getUserCategories(userId);

			List<FinancialOperation> operations = repository
				.findAllByCategoryInOrderByDateDesc(categories);

			return Utilities.getByDateBetween(operations, start, end);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public List<FinancialOperation> findAllForFamilyMemberBetweenDates(String userId,
		String familyMemberId, Date start, Date end) {
		if (userService.isUserExistsById(userId)) {

			List<Category> categories = getFamilyMemberCategories(familyMemberId);
			List<FinancialOperation> operations = repository
				.findAllByCategoryInOrderByDateDesc(categories);

			return Utilities.getByDateBetween(operations, start, end);
		}
		return Collections.emptyList();
	}

	/**
	 * Create financial operation "by hands". Creation is possible only today or later.
	 *
	 * @return FinancialOperation entity
	 */
	@Override
	public FinancialOperation create(FinancialOperationDto dto) {
		FinancialOperation operation = mapper.fromFinancialOperationDto(dto);

		Date currentDate = Utilities.removeTime(new Date());

		if (operation.getDate().after(new Date(currentDate.getTime() - 1000))) {
			return repository.save(operation);
		} else {
			throw new FinancialOperationException("Wrong date of financial operation creation.");
		}
	}

	@Override
	public FinancialOperation update(FinancialOperationDto dto) {
		FinancialOperation operation = mapper.fromFinancialOperationDto(dto);

		Date currentDate = Utilities.removeTime(new Date());
		FinancialOperation oldOperation = findById(dto.getId());

		if (oldOperation.isPlanned() && !operation.getDate()
			.after(new Date(currentDate.getTime() - 1000))) {
			throw new FinancialOperationException("Wrong date of financial operation creation.");
		}
		return repository.save(operation);
	}

	@Override
	public Boolean delete(String operationId) {
		repository.deleteById(operationId);
		return !repository.existsById(operationId);
	}

	@Override
	public FinancialOperation findById(String operationId) {
		return repository.findById(operationId).orElse(null);
	}


	private List<Category> getUserCategories(String userId) {
		List<FamilyMember> members = familyMemberService.findAll(userId);
		return categoryService.findAllByFamilyMemberIn(members);
	}

	private List<Category> getFamilyMemberCategories(String familyMemberId) {
		return categoryService.findAll(familyMemberId);
	}


}
