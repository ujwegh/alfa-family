package ru.nik.alfafamily.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FamilyMember;

public interface CategoryRepository extends MongoRepository<Category, String> {

	Category findByFamilyMember_IdAndName(String familyMemberId, String name);

	List<Category> findAllByFamilyMember_Id(String familyMemberId);

	List<Category> findAllByFamilyMemberIn(List<FamilyMember> members);

	List<Category> findAllByFamilyMember_IdAndNameIn(String familyMemberId, List<String> names);

	Long deleteByFamilyMember_IdAndName(String familyMemberId, String name);

	boolean existsByFamilyMember_IdAndName(String familyMemberId, String name);

}
