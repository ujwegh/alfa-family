package ru.nik.alfafamily.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.exceptions.FamilyMemberDoesNotExistsException;
import ru.nik.alfafamily.repository.FamilyMemberRepository;

@Service
public class FamilyMemberServiceImpl implements FamilyMemberService {

	private final FamilyMemberRepository repository;

	private final UserService userService;

	private final CategoryService categoryService;

	private final FamilyMemberPropertiesService propertiesService;

	@Autowired
	public FamilyMemberServiceImpl(FamilyMemberRepository repository, UserService service,
		CategoryService categoryService,
		FamilyMemberPropertiesService propertiesService) {
		this.repository = repository;
		this.userService = service;
		this.categoryService = categoryService;
		this.propertiesService = propertiesService;
	}


	@Override
	public List<FamilyMember> findAll(String userId) {
		return repository.findAllByUser_Id(userId);
	}

	@Override
	public FamilyMember create(String userId, String name) {
		if (userService.isUserExists(userId)){
			User user = userService.findByEmail(userId);
			FamilyMember familyMember = new FamilyMember();
			familyMember.setUser(user);
			familyMember.setName(name);
			return familyMember;
		}
		return null;
	}

	@Override
	public FamilyMember update(String userId, String familyMemberId, String name) {
		FamilyMember familyMember = findById(userId, familyMemberId);
		familyMember.setName(name);
		return null;
	}

	@Override
	public Boolean delete(String userId, String familyMemberId) {
		return repository.deleteByUser_IdAndId(userId, familyMemberId) != 0;
	}

	@Override
	public FamilyMember findById(String userId, String familyMemberId) {
		FamilyMember member = repository.findByUser_IdAndId(userId, familyMemberId);
		if (member == null) {
			throw new FamilyMemberDoesNotExistsException(
				"Family member with id " + familyMemberId + " doesn't exists.");
		}
		return member;
	}

	/**
	 * Update members categories by adding new ones and reusing existed ones
	 *
	 * @param userId - authenticated user id
	 * @param familyMemberId - id of the family member
	 * @param categories - category list
	 * @return member with updated categories
	 */
	@Override
	public FamilyMember updateCategories(String userId, String familyMemberId, List<String> categories) {
		FamilyMember member = findById(userId, familyMemberId);
		List<Category> existedCategories = categoryService.findAll(familyMemberId, categories);

		Map<String, Category> categoryMap = new HashMap<>();
		categories.forEach(newCategoryName -> categoryMap.put(newCategoryName, new Category(newCategoryName, null)));

		// update categories with existed ones
		categoryMap.forEach((k,v) -> existedCategories.forEach(existedCategory -> {
			if (k.equals(existedCategory.getName())){
				categoryMap.put(k, existedCategory);
			}
		}));

		// new categories to save
		List<Category> newCategories = new ArrayList<>();
		categoryMap.forEach((k,v) -> {
			if (v.getId() == null){
				newCategories.add(v);
			}
		});

		categoryService.bulkCreate(newCategories);

		member.setCategories((List<Category>) categoryMap.values());
		return member;
	}

	@Override
	public FamilyMember updateProperties(String userId, String familyMemberId, String color) {
		FamilyMember member = findById(userId, familyMemberId);
		propertiesService.createOrUpdate(userId, familyMemberId, color);
		return member;
	}

	@Override
	public Boolean isFamilyMemberExists(String familyMemberId) {
		return repository.existsById(familyMemberId);
	}

}
