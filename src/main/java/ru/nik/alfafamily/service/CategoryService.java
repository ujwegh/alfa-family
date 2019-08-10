package ru.nik.alfafamily.service;

import java.util.List;
import ru.nik.alfafamily.domain.Category;

public interface CategoryService {

	Category create(String userId, String familyMemberId, String name);

	List<Category> bulkCreate(List<Category> categories);

	Category update(String familyMemberId, String oldName, String newName);

	Boolean delete(String familyMemberId, String name);

	List<Category> findAll(String familyMemberId);

	List<Category> findAllByNamesIn(String familyMemberId, List<String> names);

	Category findByName(String familyMemberId, String name);

	Category findById(String categoryId);


}
