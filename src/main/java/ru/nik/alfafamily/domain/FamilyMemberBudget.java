package ru.nik.alfafamily.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "family_member_budget")
public class FamilyMemberBudget extends Budget {

	private FamilyMember member;

	public FamilyMemberBudget(Budget budget) {
		super(budget);
	}
}
