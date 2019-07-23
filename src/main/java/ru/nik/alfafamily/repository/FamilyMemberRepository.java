package ru.nik.alfafamily.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nik.alfafamily.domain.FamilyMember;

public interface FamilyMemberRepository extends MongoRepository<FamilyMember, String> {

	List<FamilyMember> findAllByUser_Email(String email);

	FamilyMember findByUser_EmailAndId(String email, String id);

	Long deleteByUser_EmailAndId(String email, String id);

	boolean existsById(String id);
}
