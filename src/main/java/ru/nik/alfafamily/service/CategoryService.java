package ru.nik.alfafamily.service;

import java.util.List;
import ru.nik.alfafamily.domain.Category;

public interface CategoryService {

	Category create(String familyMemberId, String name);

	List<Category> bulkCreate(List<Category> categories);

	Category updateByName(String familyMemberId, String oldName, String newName);

	Category updateById(String categoryId, String newName);

	Boolean deleteByName(String familyMemberId, String name);

	Boolean deleteById(String categoryId);

	List<Category> findAll(String familyMemberId);

	List<Category> findAllByNamesIn(String familyMemberId, List<String> names);

	Category findByName(String familyMemberId, String name);

	Category findById(String categoryId);


}
