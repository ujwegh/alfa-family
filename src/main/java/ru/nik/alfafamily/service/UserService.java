package ru.nik.alfafamily.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.nik.alfafamily.domain.User;

public interface UserService extends UserDetailsService {

	User save(UserRegistrationDto registration);

	User update(UserDto userDto);

	User findByEmail(String email);
}
