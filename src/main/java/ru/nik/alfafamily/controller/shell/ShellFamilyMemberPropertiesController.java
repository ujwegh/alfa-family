package ru.nik.alfafamily.controller.shell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.nik.alfafamily.domain.FamilyMemberProperties;
import ru.nik.alfafamily.dto.FamilyMemberPropertiesDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.service.FamilyMemberPropertiesService;

@Slf4j
@ShellComponent
public class ShellFamilyMemberPropertiesController {

	private final FamilyMemberPropertiesService service;

	private final Mapper mapper;

	@Autowired
	public ShellFamilyMemberPropertiesController(
		FamilyMemberPropertiesService service, Mapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@ShellMethod("Get family member properties")
	public String memberproperties(@ShellOption String propertyId) {
		FamilyMemberProperties properties = service.findById(propertyId);
		if (properties == null) return "Family member properties with id: <" + propertyId +"> doest't exists.";
		FamilyMemberPropertiesDto dto = mapper.toFamilyMemberPropertiesDto(properties);
		return dto.toString();
	}


	@ShellMethod("Create family member properties")
	public String creatememberprops(@ShellOption String familyMemberId, @ShellOption String color) {

		Map<String, String> map = new HashMap<>();
		map.put("color", color);
		FamilyMemberProperties properties = service.createOrUpdate(familyMemberId, map);
		if (properties == null) return "Create Family member properties was failed";
		FamilyMemberPropertiesDto dto = mapper.toFamilyMemberPropertiesDto(properties);
		return dto.toString();
	}


	@ShellMethod("Delete family member properties")
	public String ss(@ShellOption String familyMemberId) {
		boolean b = service.delete(familyMemberId);
		return b ? "Family member properties deleted." : "Delete family member properties failed.";
	}
}
