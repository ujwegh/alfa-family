package ru.nik.alfafamily.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.exceptions.CategoryAlreadyExistsException;
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
	public Category create(String userId, String familyMemberId, String name) {
		FamilyMember member = familyMemberService.findById(familyMemberId);
		return repository.save(new Category(name, member));
	}

	@Override
	public List<Category> bulkCreate(List<Category> categories) {
		return repository.saveAll(categories);
	}

	@Override
	public Category update(String familyMemberId, String oldName, String newName) {
		if (repository.existsByFamilyMember_IdAndName(familyMemberId, newName)) {
			throw new CategoryAlreadyExistsException();
		}
		Category category = repository.findByFamilyMember_IdAndName(familyMemberId, oldName);
		category.setName(newName);
		return repository.save(category);
	}

	@Override
	public Boolean delete(String familyMemberId, String name) {
		return repository.deleteByFamilyMember_IdAndName(familyMemberId, name) != 0;
	}

	@Override
	public List<Category> findAll(String familyMemberId) {
		return repository.findAllByFamilyMember_Id(familyMemberId);
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
}
