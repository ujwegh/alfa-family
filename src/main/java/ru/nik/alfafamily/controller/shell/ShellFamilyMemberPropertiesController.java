package ru.nik.alfafamily.controller.shell;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.nik.alfafamily.service.FamilyMemberPropertiesService;

@Slf4j
@ShellComponent
public class ShellFamilyMemberPropertiesController {

	private final FamilyMemberPropertiesService service;

	@Autowired
	public ShellFamilyMemberPropertiesController(
		FamilyMemberPropertiesService service) {
		this.service = service;
	}

//	@ShellMethod("")
//	public String ss(@ShellOption String sss) {
//
//	}
//
//
//	@ShellMethod("")
//	public String ss(@ShellOption String sss) {
//
//	}
//
//
//	@ShellMethod("")
//	public String ss(@ShellOption String sss) {
//
//	}
//
//
//	@ShellMethod("")
//	public String ss(@ShellOption String sss) {
//
//	}
//
//
//	@ShellMethod("")
//	public String ss(@ShellOption String sss) {
//
//	}
//
//
//	@ShellMethod("")
//	public String ss(@ShellOption String sss) {
//
//	}
//
//
//	@ShellMethod("")
//	public String ss(@ShellOption String sss) {
//
//	}


}
