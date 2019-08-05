package ru.nik.alfafamily.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nik.alfafamily.domain.User;

public interface UserRepository extends MongoRepository<User, Integer> {

	User findByEmail(String email);

	User findById(String id);

	boolean existsById(String userId);

	boolean existsByEmail(String email);

	List<User> findAllByIdIn(List<String> ids);
}
