package ru.nik.alfafamily.service;

import java.util.Map;
import ru.nik.alfafamily.domain.FamilyMemberProperties;

public interface FamilyMemberPropertiesService {

	FamilyMemberProperties createOrUpdate(String familyMemberId, Map<String, String> properties);

	Boolean delete(String familyMemberId);

	FamilyMemberProperties findById(String propertiesId);

	FamilyMemberProperties findByFamilyMemberId(String familyMemberId);

}
