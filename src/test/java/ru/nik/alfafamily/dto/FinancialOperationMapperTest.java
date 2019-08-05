package ru.nik.alfafamily.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import org.junit.jupiter.api.Test;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FinancialOperation;

class FinancialOperationMapperTest {

	@Test
	void toFinancialOperationDto() {

		FinancialOperation operation = new FinancialOperation("a", new Date(), "доход",
			new Category("a", "продукьты", new FamilyMember()), 555.55, "RUB", 1234567890L,
			"оплата продуктов", "дороговато вышло");
		operation.setPlanned(true);

		FinancialOperationDto dto = FinancialOperationMapper.INSTANCE
			.toFinancialOperationDto(operation);

		assertNotNull(dto);
		assertEquals(operation.getId(), dto.getId());
		assertEquals(operation.getAccountNumber(), dto.getAccountNumber());
		assertEquals(operation.getCategory().getName(), dto.getCategory().getName());
		assertEquals(operation.getCategory().getFamilyMember().getId(),
			dto.getCategory().getFamilyMemberId());
		assertEquals(operation.getComment(), dto.getComment());
		assertEquals(operation.getCurrency(), dto.getCurrency());
		assertEquals(operation.getDate(), dto.getDate());
		assertEquals(operation.getSum(), dto.getSum());
		assertEquals(operation.getDescription(), dto.getDescription());
		assertEquals(operation.getAccountNumber(), dto.getAccountNumber());
		assertEquals(operation.isPlanned(), dto.getPlanned());
	}

	@Test
	void fromFinancialOperationDto() {
		FinancialOperationDto dto = new FinancialOperationDto("a", new Date(), "расход",
			new CategoryDto("c","расход", "a"), 777.77, "RUB", 1234567890L,
			"оплата продуктов", "дороговато вышло", true);

		FinancialOperation operation = FinancialOperationMapper.INSTANCE
			.fromFinancialOperationDto(dto);

		assertNotNull(operation);
		assertEquals(dto.getId(), operation.getId());
		assertEquals(dto.getAccountNumber(), operation.getAccountNumber());
		assertEquals(dto.getCategory().getName(), operation.getCategory().getName());
		assertEquals(dto.getComment(), operation.getComment());
		assertEquals(dto.getCurrency(), operation.getCurrency());
		assertEquals(dto.getDate(), operation.getDate());
		assertEquals(dto.getDescription(), operation.getDescription());
		assertEquals(dto.getSum(), operation.getSum());
		assertEquals(dto.getType(), operation.getType());
		assertEquals(dto.getPlanned(), operation.isPlanned());
	}
}