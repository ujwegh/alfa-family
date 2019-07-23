package ru.nik.alfafamily.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;
import ru.nik.alfafamily.domain.FinancialOperation;

class FinancialOperationMapperTest {

	@Test
	void toFinancialOperationDto() {

		FinancialOperation financialOperation = new FinancialOperation("a", new Date(), "доход",
			new Category("a","продукьты", new FamilyMember()), 555.55, "RUB", 1234567890L,
			"оплата продуктов", "дороговато вышло");

		FinancialOperationDto dto = FinancialOperationMapper.INSTANCE.toFinancialOperationDto(financialOperation);

		assertNotNull(dto);
		assertEquals(dto.getAccountNumber(), financialOperation.getAccountNumber());
		assertEquals(dto.getCategory().getName(), financialOperation.getCategory().getName());
		assertEquals(dto.getComment(), financialOperation.getComment());
		assertEquals(dto.getCurrency(), financialOperation.getCurrency());
		assertEquals(dto.getDate(), financialOperation.getDate());
		assertEquals(dto.getDescription(), financialOperation.getDescription());
		assertEquals(dto.getSum(), financialOperation.getSum());
		assertEquals(dto.getType(), financialOperation.getType());
		assertEquals(dto.getUuid(), financialOperation.getUuid());
	}

	@Test
	void fromFinancialOperationDto() {
		FinancialOperationDto dto = new FinancialOperationDto(UUID.randomUUID(),
			new Date(), "расход", new CategoryDto("расход", "a"), 777.77, "RUB", 1234567890L,
			"оплата продуктов", "дороговато вышло");

		FinancialOperation operation = FinancialOperationMapper.INSTANCE.fromFinancialOperationDto(dto);

		assertNotNull(operation);
		assertEquals(operation.getAccountNumber(), dto.getAccountNumber());
		assertEquals(operation.getCategory().getName(), dto.getCategory().getName());
		assertEquals(operation.getComment(), dto.getComment());
		assertEquals(operation.getCurrency(), dto.getCurrency());
		assertEquals(operation.getDate(), dto.getDate());
		assertEquals(operation.getSum(), dto.getSum());
		assertEquals(operation.getDescription(), dto.getDescription());
		assertEquals(operation.getUuid(), dto.getUuid());
		assertEquals(operation.getAccountNumber(), dto.getAccountNumber());
	}
}