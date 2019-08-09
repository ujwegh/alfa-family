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

	private String id;

	private Date date;

	private String type;

	private CategoryDto category;

	private Double sum;

	private String currency;

	private Long accountNumber;

	private String description;

	private String comment;

	private Boolean planned;

	@Override
	public String toString() {
		return "FinancialOperationDto{" +
			"id='" + id + '\'' +
			", date=" + date +
			", type='" + type + '\'' +
			", category=" + category.getName() +
			", sum=" + sum +
			", currency='" + currency + '\'' +
			", accountNumber=" + accountNumber +
			", description='" + description + '\'' +
			", comment='" + comment + '\'' +
			", planned=" + planned +
			'}';
	}
}
