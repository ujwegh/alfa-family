package ru.nik.alfafamily.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nik.alfafamily.domain.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
	Category findByMember_IdAndName(String familyMemberId, String name);

	List<Category> findAllByMember_Id(String familyMemberId);

	List<Category> findAllByMember_IdAndNameIn(String familyMemberId, List<String> names);

	Long deleteByMember_IdAndName(String familyMemberId, String name);

	boolean existsByMember_IdAndName(String familyMemberId, String name);

}
