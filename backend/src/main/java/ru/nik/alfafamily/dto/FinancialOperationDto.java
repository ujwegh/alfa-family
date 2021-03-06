package ru.nik.alfafamily.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
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

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date date;

	private String type;

	private CategoryDto category;

	private Double sum;

	private String currency;

	private Long accountNumber;

	private String description;

	private String comment;

	private Boolean planned = false;

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
