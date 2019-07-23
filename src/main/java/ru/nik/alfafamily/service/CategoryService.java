package ru.nik.alfafamily.service;

import java.util.List;
import ru.nik.alfafamily.domain.Category;

public interface CategoryService {

	Category create(String email, String familyMemberId, String name);

	List<Category> bulkCreate(List<Category> categories);

	Category updateByName(String familyMemberId, String oldName, String newName);

	Boolean deleteByName(String familyMemberId,String name);

	List<Category> findAll(String familyMemberId);

	List<Category> findAllByNameIn(String familyMemberId, List<String> names);

	Category getByName(String familyMemberId, String name);


}
