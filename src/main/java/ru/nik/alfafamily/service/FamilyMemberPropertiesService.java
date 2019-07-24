package ru.nik.alfafamily.service;

import java.util.Map;
import ru.nik.alfafamily.domain.FamilyMemberProperties;

public interface FamilyMemberPropertiesService {

	FamilyMemberProperties createOrUpdate(String userId, String familyMemberId, Map<String, String> properties);

	Boolean delete(String familyMemberId);

}
