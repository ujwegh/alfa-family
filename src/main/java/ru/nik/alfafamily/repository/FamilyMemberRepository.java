package ru.nik.alfafamily.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nik.alfafamily.domain.FamilyMember;

public interface FamilyMemberRepository extends MongoRepository<FamilyMember, String> {

	List<FamilyMember> findAllByUser_Id(String userId);

	FamilyMember findByUser_IdAndId(String userId, String id);

	Long deleteByUser_IdAndId(String userId, String id);

	boolean existsById(String id);
}
