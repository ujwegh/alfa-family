package ru.nik.alfafamily.controller.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nik.alfafamily.domain.FamilyMemberProperties;
import ru.nik.alfafamily.dto.FamilyMemberPropertiesDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.service.FamilyMemberPropertiesService;

@Api(value="Family member properties rest controller", description="Properties manager of current family member")
@Slf4j
@RestController
@RequestMapping("/rest/{userId}/properties")
public class FamilyMemberPropertiesRestController {

	private final FamilyMemberPropertiesService service;

	private final Mapper mapper;

	@Autowired
	public FamilyMemberPropertiesRestController(FamilyMemberPropertiesService service,
		Mapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Update family members properties", response = FamilyMemberPropertiesDto.class)
	@PostMapping
	public FamilyMemberPropertiesDto update(@PathVariable @Nonnull final String userId,
		@RequestBody FamilyMemberPropertiesDto dto) {
		log.info("Update family members properties: {}", dto.toString());
		Map<String, String> map = new HashMap<>();
		map.put("color", dto.getColor());
		FamilyMemberProperties properties = service.createOrUpdate(dto.getFamilyMemberId(), map);
		return properties != null ? mapper.toFamilyMemberPropertiesDto(properties) : null;
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Delete family member properties")
	@DeleteMapping("/{familyMemberId}")
	public void delete(@PathVariable @Nonnull final String userId,
		@PathVariable @Nonnull final String familyMemberId) {
		log.info("Delete family member properties");
		service.delete(familyMemberId);
	}

	@PreAuthorize("@auth.mayGetAccess(principal, #userId)")
	@ApiOperation(value = "Get family member properties by id", response = FamilyMemberPropertiesDto.class)
	@GetMapping("/{familyMemberId}")
	public FamilyMemberPropertiesDto get(@PathVariable @Nonnull final String userId,
		@PathVariable @Nonnull final String familyMemberId) {
		log.info("Get family member properties by id: ", familyMemberId);
		FamilyMemberProperties properties = service.findById(familyMemberId);
		return properties != null ? mapper.toFamilyMemberPropertiesDto(properties) : null;
	}

}
