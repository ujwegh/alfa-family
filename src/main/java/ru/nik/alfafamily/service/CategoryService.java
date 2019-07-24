package ru.nik.alfafamily.service;

import java.util.List;
import ru.nik.alfafamily.domain.Category;

public interface CategoryService {

	Category create(String userId, String familyMemberId, String name);

	List<Category> bulkCreate(List<Category> categories);

	Category update(String familyMemberId, String oldName, String newName);

	Boolean delete(String familyMemberId, String name);

	List<Category> findAll(String familyMemberId);

	List<Category> findAll(String familyMemberId, List<String> names);

	Category get(String familyMemberId, String name);


}
