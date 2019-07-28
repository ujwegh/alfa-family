package ru.nik.alfafamily.domain;

import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@ToString
@NoArgsConstructor
@Document(collection = "financial_operations")
public class FinancialOperation {

	@Id
	private String id;

	private UUID uuid = UUID.randomUUID();

	private Date date;

	private String type;

	@DBRef
	private Category category;

	private Double sum;

	private String currency;

	private Long accountNumber;

	private String description;

	private String comment;

	private boolean planned;


	public FinancialOperation(String id, Date date, String type,
		Category category, Double sum, String currency, Long accountNumber,
		String description, String comment) {
		this.id = id;
		this.date = date;
		this.type = type;
		this.category = category;
		this.sum = sum;
		this.currency = currency;
		this.accountNumber = accountNumber;
		this.description = description;
		this.comment = comment;
	}


	public FinancialOperation(Date date, String type, Category category, Double sum,
		String currency, Long accountNumber, String description, String comment) {
		this.date = date;
		this.type = type;
		this.category = category;
		this.sum = sum;
		this.currency = currency;
		this.accountNumber = accountNumber;
		this.description = description;
		this.comment = comment;
	}
}
