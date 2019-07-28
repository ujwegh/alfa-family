package ru.nik.alfafamily.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "budget")
public class Budget {

	@Id
	private String id;

	private Double income;

	private Double outcome;

	@DBRef
	private User user;

	public Budget(Budget budget) {
		this.id = budget.getId();
		this.income = budget.getIncome();
		this.outcome = budget.getOutcome();
		this.user = budget.getUser();
	}
}
