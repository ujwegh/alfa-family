package ru.nik.alfafamily.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nik.alfafamily.domain.Budget;
import ru.nik.alfafamily.dto.BudgetDto;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.service.BudgetService;

@Slf4j
@RestController
@RequestMapping("/rest/budget")
public class BudgetRestController {


	private final BudgetService service;

	private final Mapper mapper;

	@Autowired
	public BudgetRestController(BudgetService service, Mapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping("/count")
	public BudgetDto budget(@RequestBody BudgetDto dto) {
		log.info("Count budget for user: {} and family member: {}", dto.getUserId(), dto.getFamilyMemberId());
		Budget budget;
		if (dto.getFamilyMemberId() == null) {
			budget = service.countForUserBetweenDates(dto.getUserId(), dto.getStartDate(), dto.getEndDate());
		} else {
			budget = service.countForFamilyMemberBetweenDates(dto.getUserId(), dto.getFamilyMemberId(),
				dto.getStartDate(), dto.getEndDate());
		}
		return mapper.toBudgetDto(budget);
	}
}
