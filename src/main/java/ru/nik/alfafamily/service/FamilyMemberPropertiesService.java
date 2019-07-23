package ru.nik.alfafamily.service;

import ru.nik.alfafamily.domain.FamilyMemberProperties;

public interface FamilyMemberPropertiesService {

	FamilyMemberProperties createOrUpdate(String email, String familyMemberId, String color);

	Boolean delete(String familyMemberId);

}
