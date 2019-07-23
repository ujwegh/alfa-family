package ru.nik.alfafamily.service;

import java.util.List;
import ru.nik.alfafamily.domain.FamilyMember;

public interface FamilyMemberService {

	List<FamilyMember> findAll(String userId);

	FamilyMember create(String userId, String name);

	FamilyMember update(String userId, String familyMemberId, String name);

	Boolean delete(String userId, String familyMemberId);

	FamilyMember findById(String userId, String familyMemberId);

	FamilyMember updateCategories(String userId, String familyMemberId, List<String> categories);

	FamilyMember updateProperties(String userId, String familyMemberId, String color);

	Boolean isFamilyMemberExists(String familyMemberId);

}
