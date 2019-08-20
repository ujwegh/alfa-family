package ru.nik.alfafamily.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nik.alfafamily.domain.FamilyMemberProperties;

public interface FamilyMemberPropertiesRepository extends MongoRepository<FamilyMemberProperties, String> {

	int deleteByFamilyMember_Id(String familyMemberId);

	FamilyMemberProperties findByFamilyMember_Id(String id);
}
