package ru.nik.alfafamily.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FinancialOperation;

@Mapper
public interface FinancialOperationMapper {

	FinancialOperationMapper INSTANCE = Mappers.getMapper(FinancialOperationMapper.class);

	@Mapping(source = "planned", target = "planned")
	FinancialOperationDto toFinancialOperationDto(FinancialOperation operation);

	@Mapping(source = "planned", target = "planned")
	FinancialOperation fromFinancialOperationDto(FinancialOperationDto operationDto);

}
