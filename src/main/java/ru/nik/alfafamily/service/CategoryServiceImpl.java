package ru.nik.alfafamily.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
		FamilyMemberService familyMemberService) {
		this.repository = repository;
		this.familyMemberService = familyMemberService;
	}

	@Override
	public Category create(String email, String familyMemberId, String name) {
		FamilyMember member = familyMemberService.findById(email, familyMemberId);
		return repository.save(new Category(name, member));
	}

	@Override
	public List<Category> bulkCreate(List<Category> categories) {
		return repository.saveAll(categories);
	}

	@Override
	public Category updateByName(String familyMemberId, String oldName, String newName) {
		if (repository.existsByMember_IdAndName(familyMemberId, newName)){
			throw new CategoryAlreadyExistsException();
		}
		Category category = repository.findByMember_IdAndName(familyMemberId, oldName);
		category.setName(newName);
		return repository.save(category);
	}

	@Override
	public Boolean deleteByName(String familyMemberId, String name) {
		return repository.deleteByMember_IdAndName(familyMemberId, name) != 0;
	}

	@Override
	public List<Category> findAll(String familyMemberId) {
		return repository.findAllByMember_Id(familyMemberId);
	}

	@Override
	public List<Category> findAllByNameIn(String familyMemberId, List<String> names) {
		return repository.findAllByMember_IdAndNameIn(familyMemberId, names);
	}

	@Override
	public Category getByName(String familyMemberId, String name) {
		return repository.findByMember_IdAndName(familyMemberId, name);
	}
}
