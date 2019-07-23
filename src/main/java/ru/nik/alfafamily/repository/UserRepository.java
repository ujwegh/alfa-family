package ru.nik.alfafamily.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nik.alfafamily.domain.User;

public interface UserRepository extends MongoRepository<User, Integer> {

	User findByEmail(String email);

	boolean existsById(String userId);
}
