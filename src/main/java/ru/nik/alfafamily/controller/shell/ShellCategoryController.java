package ru.nik.alfafamily.controller.shell;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.dto.CategoryDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.service.CategoryService;

@Slf4j
@ShellComponent
public class ShellCategoryController {

	private final CategoryService service;

	private final Mapper mapper;

	@Autowired
	public ShellCategoryController(CategoryService service, Mapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@ShellMethod("Get category by id")
	public String category(@ShellOption String categoryId){
		Category category = service.findById(categoryId);
		if (category == null) return "Category with id: " + categoryId+ " doesn't exists.";
		CategoryDto dto = mapper.toCategoryDto(category);
		return "New category: " + dto.toString();
	}

	@ShellMethod("Get all categories in names")
	public String categories_by_names(@ShellOption String familyMemberId, @ShellOption String categories){
		List<String> categoryList;
		if (categories.contains(",")) {
			categoryList = Arrays.asList(categories.split(","));
		} else {
			categoryList = Collections.singletonList(categories);
		}

		List<Category> cats = service.findAllByNamesIn(familyMemberId, categoryList);
		if (cats == null) return "Find all categories by names was failed.";
		List<CategoryDto> dtos = cats.stream().map(mapper::toCategoryDto)
			.collect(Collectors.toList());
		return "Categories by names: \n" + dtos.toString();
	}

	@ShellMethod("Get all categories")
	public String categories(@ShellOption String familyMemberId){
		List<Category> cats = service.findAll(familyMemberId);
		if (cats == null) return "Find all categories by names was failed.";
		List<CategoryDto> dtos = cats.stream().map(mapper::toCategoryDto)
			.collect(Collectors.toList());
		return "Categories by names: \n" + dtos.toString();
	}

	@ShellMethod("Create new category")
	public String new_category(@ShellOption String familyMemberId, @ShellOption String name){

		Category category = service.create(familyMemberId, name);

		if (category == null) return "Create new category: <"+ name+ "> was failed.";
		CategoryDto dto = mapper.toCategoryDto(category);
		return "New category: " + dto.toString();
	}

	@ShellMethod("Delete category by name")
	public String delete_category(@ShellOption String familyMemberId, @ShellOption String name){
		boolean b = service.deleteByName(familyMemberId, name);
		return b ? "Category deleted." : "Delete category failed.";
	}



	@ShellMethod("Get category by name")
	public String category_by_name(@ShellOption String familyMemberId, @ShellOption String name){
		Category category = service.findByName(familyMemberId, name);
		if (category == null) return "Category: <"+ name+ "> doesn't exists.";
		CategoryDto dto = mapper.toCategoryDto(category);
		return dto.toString();
	}

	@ShellMethod("Update category")
	public String update_category(@ShellOption String familyMemberId, @ShellOption String oldName,
		@ShellOption String newName){
		Category category = service.updateByName(familyMemberId, oldName, newName);
		if (category == null) return "Category updateByName was failed.";
		CategoryDto dto = mapper.toCategoryDto(category);
		return "Category updated:" + dto.toString();
	}
}
