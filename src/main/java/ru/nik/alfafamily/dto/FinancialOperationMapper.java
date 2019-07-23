package ru.nik.alfafamily.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FinancialOperation;

@Mapper
public interface FinancialOperationMapper {

	FinancialOperationMapper INSTANCE = Mappers.getMapper(FinancialOperationMapper.class);

	FinancialOperationDto toFinancialOperationDto(FinancialOperation operation);

	FinancialOperation fromFinancialOperationDto(FinancialOperationDto operationDto);

}
