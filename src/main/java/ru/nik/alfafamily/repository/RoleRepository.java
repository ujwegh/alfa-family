package ru.nik.alfafamily.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nik.alfafamily.domain.Role;

public interface RoleRepository extends MongoRepository<Role, String> {

	Role findByName(String name);

	List<Role> findAllByNameIn(String... names);
}
