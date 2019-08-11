package ru.nik.alfafamily.domain;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
public class Budget {

	@Id
	private String id;

	private String userId;

	private String familyMemberId;

	private Double income;

	private Double outcome;

	private Date startDate;

	private Date endDate;

	public Budget(String userId, String familyMemberId, Double income, Double outcome,
		Date startDate, Date endDate) {
		this.userId = userId;
		this.familyMemberId = familyMemberId;
		this.income = income;
		this.outcome = outcome;
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
