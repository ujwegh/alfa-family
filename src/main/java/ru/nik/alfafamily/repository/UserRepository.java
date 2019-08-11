package ru.nik.alfafamily.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nik.alfafamily.domain.User;

public interface UserRepository extends MongoRepository<User, String> {

	User findByEmail(String email);

	boolean existsById(String userId);

	boolean existsByEmail(String email);

	List<User> findAllByIdIn(List<String> ids);
}
