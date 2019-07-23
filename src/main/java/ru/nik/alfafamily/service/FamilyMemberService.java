package ru.nik.alfafamily.service;

import java.util.List;
import ru.nik.alfafamily.domain.FamilyMember;

public interface FamilyMemberService {

	List<FamilyMember> findAll(String email);

	FamilyMember create(String email, String name);

	FamilyMember update(String email, String familyMemberId, String name);

	Boolean delete(String email, String familyMemberId);

	FamilyMember findById(String email, String familyMemberId);

	FamilyMember updateCategories(String email, String familyMemberId, List<String> categories);

	FamilyMember updateProperties(String email, String familyMemberId, String color);

	Boolean isFamilyMemberExists(String familyMemberId);

}
