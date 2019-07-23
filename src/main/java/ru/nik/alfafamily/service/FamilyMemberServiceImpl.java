package ru.nik.alfafamily.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FamilyMemberProperties;
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
	public List<FamilyMember> findAll(String email) {
		return repository.findAllByUser_Email(email);
	}

	@Override
	public FamilyMember create(String email, String name) {
		if (userService.isUserExists(email)){
			User user = userService.findByEmail(email);
			FamilyMember familyMember = new FamilyMember();
			familyMember.setUser(user);
			familyMember.setName(name);
			return familyMember;
		}
		return null;
	}

	@Override
	public FamilyMember update(String email, String familyMemberId, String name) {
		FamilyMember familyMember = findById(email, familyMemberId);
		familyMember.setName(name);
		return null;
	}

	@Override
	public Boolean delete(String email, String familyMemberId) {
		return repository.deleteByUser_EmailAndId(email, familyMemberId) != 0;
	}

	@Override
	public FamilyMember findById(String email, String familyMemberId) {
		FamilyMember member = repository.findByUser_EmailAndId(email, familyMemberId);
		if (member == null) {
			throw new FamilyMemberDoesNotExistsException(
				"Family member with id " + familyMemberId + " doesn't exists.");
		}
		return member;
	}

	/**
	 * Update members categories by adding new ones and reusing existed ones
	 *
	 * @param email - users email
	 * @param familyMemberId - id of the family member
	 * @param categories - category list
	 * @return member with updated categories
	 */
	@Override
	public FamilyMember updateCategories(String email, String familyMemberId, List<String> categories) {
		FamilyMember member = findById(email, familyMemberId);
		List<Category> existedCategories = categoryService.findAllByNameIn(familyMemberId, categories);

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
	public FamilyMember updateProperties(String email, String familyMemberId, String color) {
		FamilyMember member = findById(email, familyMemberId);
		propertiesService.createOrUpdate(email, familyMemberId, color);
		return member;
	}

	@Override
	public Boolean isFamilyMemberExists(String familyMemberId) {
		return repository.existsById(familyMemberId);
	}

}
