package ru.nik.alfafamily.service;

import ru.nik.alfafamily.domain.FamilyMemberProperties;

public interface FamilyMemberPropertiesService {

	FamilyMemberProperties createOrUpdate(String userId, String familyMemberId, String color);

	Boolean delete(String familyMemberId);

}
