package ru.nik.alfafamily.controller.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.dto.CategoryDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.service.CategoryService;

@Api(value="Category rest controller", description="Category manager of current family member")
@Slf4j
@RestController
@RequestMapping("/rest/{userId}/categories")
public class CategoryRestController {

	private final CategoryService service;

	private final Mapper mapper;


	public CategoryRestController(CategoryService service, Mapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Get all categories for family member", response = List.class)
	@GetMapping("/{familyMemberId}")
	public List<CategoryDto> findAll(@PathVariable @Nonnull final String userId,
		@PathVariable @Nonnull final String familyMemberId) {
		log.info("Find all categories for family member: {}", familyMemberId);
		List<Category> categories = service.findAll(familyMemberId);
		List<CategoryDto> dtos = new ArrayList<>();
		categories.forEach(category -> dtos.add(mapper.toCategoryDto(category)));
		return dtos;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Find category by id", response = CategoryDto.class)
	@GetMapping("/category/{categoryId}")
	public CategoryDto findById(@PathVariable @Nonnull final String userId,
		@PathVariable @Nonnull final String categoryId) {
		log.info("Find category by id: {}", categoryId);
		Category category = service.findById(categoryId);
		return category != null ? mapper.toCategoryDto(category) : null;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Find category by name", response = CategoryDto.class)
	@GetMapping("/{familyMemberId}/category/{name}")
	public CategoryDto findByName(@PathVariable @Nonnull final String userId,
		@PathVariable @Nonnull final String familyMemberId, @PathVariable String name) {
		log.info("Find category by name: {} from family member: {}", name, familyMemberId);
		Category category = service.findByName(familyMemberId, name);
		return category != null ? mapper.toCategoryDto(category) : null;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Create new category", response = CategoryDto.class)
	@PostMapping
	public CategoryDto create(@PathVariable @Nonnull final String userId, @RequestBody CategoryDto dto) {
		log.info("Create new category: {}", dto.toString());
		Category category = service.create(dto.getFamilyMemberId(), dto.getName());
		return category != null ? mapper.toCategoryDto(category) : null;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Update category", response = CategoryDto.class)
	@PutMapping
	public CategoryDto update(@PathVariable @Nonnull final String userId, @RequestBody CategoryDto dto) {
		log.info("Update category: {}", dto.toString());
		Category category = service.updateById(dto.getId(), dto.getName());
		return category != null ? mapper.toCategoryDto(category) : null;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Delete category by name")
	@DeleteMapping("/{familyMemberId}/category/{name}")
	public void delete(@PathVariable @Nonnull final String userId,
		@PathVariable @Nonnull final String familyMemberId, @PathVariable String name) {
		log.info("Delete category by name: {} from family member: {}", name, familyMemberId);
		service.deleteByName(familyMemberId, name);
	}
}
