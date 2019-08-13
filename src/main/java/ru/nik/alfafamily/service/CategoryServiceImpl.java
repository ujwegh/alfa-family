package ru.nik.alfafamily.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.exceptions.CategoryDoesntExistsException;
import ru.nik.alfafamily.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository repository;


	private final FamilyMemberService familyMemberService;

	@Autowired
	public CategoryServiceImpl(CategoryRepository repository,
		@Lazy FamilyMemberService familyMemberService) {
		this.repository = repository;
		this.familyMemberService = familyMemberService;
	}

	@Override
	public Category create(String familyMemberId, String name) {
		FamilyMember member = familyMemberService.findById(familyMemberId);
		//		if (category.getName() != null) {
//			familyMemberService.updateCategories(member.getId(),
//				Collections.singletonList(category.getName()));
//		}
		return repository.save(new Category(name, member));
	}

	@Override
	public List<Category> bulkCreate(List<Category> categories) {
		return repository.saveAll(categories);
	}

	@Override
	public Category updateByName(String familyMemberId, String oldName, String newName) {
		if (repository.existsByFamilyMember_IdAndName(familyMemberId, newName)) {
			throw new CategoryDoesntExistsException(
				"Category with name: " + oldName + " for family member: "
					+ familyMemberId + " doesn't exists.");
		}
		Category category = repository.findByFamilyMember_IdAndName(familyMemberId, oldName);
		category.setName(newName);
		return repository.save(category);
	}

	@Override
	public Category updateById(String categoryId, String newName) {
		Category category = repository.findById(categoryId).orElse(null);
		if (category == null) {
			throw new CategoryDoesntExistsException(
				"Category with id: " + categoryId + " doesn't exists.");
		}
		category.setName(newName);
		return repository.save(category);
	}

	@Override
	public Boolean deleteByName(String familyMemberId, String name) {
		return repository.deleteByFamilyMember_IdAndName(familyMemberId, name) != 0;
	}

	@Override
	public Boolean deleteById(String categoryId) {
		boolean b = true;
		try {
			repository.deleteById(categoryId);
		} catch (IllegalArgumentException e) {
			b = false;
		}
		return b;
	}

	@Override
	public List<Category> findAll(String familyMemberId) {
		return repository.findAllByFamilyMember_Id(familyMemberId);
	}

	@Override
	public List<Category> findAllByFamilyMemberIn(List<FamilyMember> familyMembers) {
		if (!familyMembers.isEmpty())
			return repository.findAllByFamilyMemberIn(familyMembers);
		return Collections.emptyList();
	}

	@Override
	public List<Category> findAllByNamesIn(String familyMemberId, List<String> names) {
		return repository.findAllByFamilyMember_IdAndNameIn(familyMemberId, names);
	}

	@Override
	public Category findByName(String familyMemberId, String name) {
		return repository.findByFamilyMember_IdAndName(familyMemberId, name);
	}

	@Override
	public Category findById(String categoryId) {
		return repository.findById(categoryId).orElse(null);
	}

	@Override
	public List<Category> bulkUpdate(String familyMemberId, List<String> categories) {
		FamilyMember member = familyMemberService.findById(familyMemberId);
		List<Category> existedCategories = findAllByNamesIn(familyMemberId, categories);


		List<Category> newCategories = new ArrayList<>();

		List<String> existedCategoriesNames = existedCategories.stream().map(Category::getName)
			.collect(Collectors.toList());

		categories.forEach(category -> {
			if (!existedCategoriesNames.contains(category)) {
				newCategories.add(new Category(category, member));
			}
		});

		List<Category> savedCategories = bulkCreate(newCategories);

		List<Category> result = new ArrayList<>();
		result.addAll(existedCategories);
		result.addAll(savedCategories);
		return result;



//		Map<String, Category> categoryMap = new HashMap<>();
//		categories.forEach(newCategoryName -> categoryMap.put(newCategoryName, new Category(newCategoryName, null)));
//
//		// updateByName categories with existed ones
//		categoryMap.forEach((k,v) -> existedCategories.forEach(existedCategory -> {
//			if (k.equals(existedCategory.getName())){
//				categoryMap.put(k, existedCategory);
//			}
//		}));
//
//		// new categories to save
//		List<Category> newCategories = new ArrayList<>();
//		categoryMap.forEach((k,v) -> {
//			if (v.getId() == null){
//				newCategories.add(v);
//			}
//		});
//
//		bulkCreate(newCategories);

//		return null;
	}
}
