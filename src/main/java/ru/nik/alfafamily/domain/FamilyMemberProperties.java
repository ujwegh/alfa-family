package ru.nik.alfafamily.domain;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "family_member_properties")
public class FamilyMemberProperties {

	@Id
	private String id;

	@DBRef
	private FamilyMember familyMember;

//	private Map<String, String> props;

	private String color;

	public FamilyMemberProperties(FamilyMember familyMember, String color) {
		this.familyMember = familyMember;
		this.color = color;
	}
}
