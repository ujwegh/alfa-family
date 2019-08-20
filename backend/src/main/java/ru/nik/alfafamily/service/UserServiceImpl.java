package ru.nik.alfafamily.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.Mapper;
import ru.nik.alfafamily.dto.UserDto;
import ru.nik.alfafamily.dto.UserRegistrationDto;
import ru.nik.alfafamily.exceptions.RoleDoesntExistsException;
import ru.nik.alfafamily.exceptions.UserDoesNotExistsException;
import ru.nik.alfafamily.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final RoleService roleService;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	private final Mapper mapper;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, RoleService roleService,
		@Lazy BCryptPasswordEncoder bCryptPasswordEncoder, Mapper mapper) {
		this.userRepository = userRepository;
		this.roleService = roleService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.mapper = mapper;
	}

	@Override
	public User save(UserRegistrationDto registration) {

		Role role = roleService.getByName("ROLE_USER");
		if (role == null) {
			role = new Role("ROLE_USER");
			roleService.create(role.getName());
		}

		User user = new User();
		user.setFirstName(registration.getFirstName());
		user.setLastName(registration.getLastName());
		user.setEmail(registration.getEmail());
		user.setPassword(bCryptPasswordEncoder.encode(registration.getPassword()));
		user.setRole(role);
		return userRepository.save(user);
	}

	@Override
	public User update(UserDto userDto) {
		User user = userRepository.findById(userDto.getId()).orElse(null);
		if (user == null) {
			throw new UserDoesNotExistsException(
				"User with id: " + userDto.getId() + " doesn't exist.");
		}
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setPassword(userDto.getPassword());
		user.setEmail(userDto.getEmail());
		user.setEnabled(userDto.isEnabled());

		String roleName = userDto.getRole().getName();
		Role role;
		if (roleService.isExistsByName(roleName)) {
			role = roleService.getByName(roleName);
		} else {
			throw new RoleDoesntExistsException(roleName);
		}
		user.setRole(role);
		return userRepository.save(user);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User findById(String userId) {
		return userRepository.findById(userId).orElse(null);
	}

	@Override
	public Boolean isUserExistsById(String userId) {
		if (!userRepository.existsById(userId)) {
			throw new UserDoesNotExistsException("User with ID <" + userId + "> doesn't exist.");
		}
		return true;
	}

	@Override
	public Boolean isUserExistsByEmail(String email) {
		if (!userRepository.existsByEmail(email)) {
			throw new UserDoesNotExistsException("User with email <" + email + "> doesn't exist.");
		}
		return true;
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public List<User> findAllByIdIn(List<String> ids) {
		return userRepository.findAllByIdIn(ids);
	}

	@Override
	public void delete(String userId) {
		userRepository.deleteById(userId);
	}

	@HystrixCommand(fallbackMethod = "getDefaultUser")
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(),
			user.getPassword(),
			mapRolesToAuthorities(Collections.singleton(user.getRole())));
	}

	public UserDetails getDefaultUser(String email) {
		User user = new User();
		user.setEmail("user");
		user.setFirstName("user");
		user.setLastName("");
		user.setPassword(bCryptPasswordEncoder.encode("password"));
		user.setRole(new Role("ROLE_DEFAULT"));

		return new org.springframework.security.core.userdetails.User(user.getEmail(),
			user.getPassword(),
			mapRolesToAuthorities(Collections.singleton(user.getRole())));
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream()
			.map(role -> new SimpleGrantedAuthority(role.getName()))
			.collect(Collectors.toList());
	}
}
