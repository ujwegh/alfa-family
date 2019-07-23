package ru.nik.alfafamily.dto;

import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinancialOperationDto {

	private UUID uuid;

	private Date date;

	private String type;

	private CategoryDto category;

	private Double sum;

	private String currency;

	private Long accountNumber;

	private String description;

	private String comment;

}
