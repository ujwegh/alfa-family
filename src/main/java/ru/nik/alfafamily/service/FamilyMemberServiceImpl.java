package ru.nik.alfafamily.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
		@Lazy CategoryService categoryService,
		@Lazy FamilyMemberPropertiesService propertiesService) {
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
		if (userService.isUserExistsById(userId)){
			User user = userService.findById(userId);
			FamilyMember familyMember = new FamilyMember();
			familyMember.setUser(user);
			familyMember.setName(name);
			return familyMember;
		}
		return null;
	}

	@Override
	public FamilyMember update(String familyMemberId, String name) {
		FamilyMember familyMember = findById(familyMemberId);
		familyMember.setName(name);
		return null;
	}

	@Override
	public Boolean delete(String familyMemberId) {
		repository.deleteById(familyMemberId);

		return true;
	}

	@Override
	public FamilyMember findById(String familyMemberId) {
		if (!isFamilyMemberExists(familyMemberId))
			throw new FamilyMemberDoesNotExistsException(
				"Family familyMember with id " + familyMemberId + " doesn't exists.");
		return repository.findById(familyMemberId).orElse(null);
	}

	/**
	 * Update members categories by adding new ones and reusing existed ones
	 *
	 * @param familyMemberId - id of the family familyMember
	 * @param categories - category list
	 * @return familyMember with updated categories
	 */
	@Override
	public FamilyMember updateCategories(String familyMemberId, List<String> categories) {
		FamilyMember member = findById(familyMemberId);
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
	public FamilyMember updateProperties(String familyMemberId, String color) {
		FamilyMember member = findById(familyMemberId);
		Map<String, String> map = new HashMap<>();
		map.put("color", color);
		propertiesService.createOrUpdate(familyMemberId, map);
		return member;
	}

	@Override
	public Boolean isFamilyMemberExists(String familyMemberId) {
		return repository.existsById(familyMemberId);
	}

}
