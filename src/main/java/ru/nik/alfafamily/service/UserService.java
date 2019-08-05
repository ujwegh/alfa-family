package ru.nik.alfafamily.service;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.UserDto;
import ru.nik.alfafamily.dto.UserRegistrationDto;

public interface UserService extends UserDetailsService {

	User save(UserRegistrationDto registration);

	User update(UserDto userDto);

	User findByEmail(String email);

	User findById(String userId);

	Boolean isUserExistsById(String userId);

	Boolean isUserExistsByEmail(String email);

	List<User> findAll();

	List<User> findAllByIdIn(List<String> ids);
}
