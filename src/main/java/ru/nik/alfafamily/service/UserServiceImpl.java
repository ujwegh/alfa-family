package ru.nik.alfafamily.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import ru.nik.alfafamily.dto.RoleDto;
import ru.nik.alfafamily.dto.UserDto;
import ru.nik.alfafamily.dto.UserRegistrationDto;
import ru.nik.alfafamily.exceptions.UserDoesNotExistsException;
import ru.nik.alfafamily.repository.RoleRepository;
import ru.nik.alfafamily.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	private final Mapper mapper;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
		@Lazy BCryptPasswordEncoder bCryptPasswordEncoder, Mapper mapper) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.mapper = mapper;
	}

	@Override
	public User save(UserRegistrationDto registration) {

		Role role = roleRepository.findByName("ROLE_USER");
		if (role == null) {
			role = new Role("ROLE_USER");
			roleRepository.save(role);
		}

		User user = new User();
		user.setFirstName(registration.getFirstName());
		user.setLastName(registration.getLastName());
		user.setEmail(registration.getEmail());
		user.setPassword(bCryptPasswordEncoder.encode(registration.getPassword()));
		user.setRoles(Collections.singletonList(role));
		return userRepository.save(user);
	}

	@Override
	public User update(UserDto userDto) {
		User user = userRepository.findById(userDto.getId());
		User newUser = mapper.fromUserDto(userDto);
		newUser.setMembers(user.getMembers());

		List<Role> allRoles = (List<Role>) user.getRoles();

		List<Role> toSaveRoles = allRoles.stream().filter(role -> role.getId() == null)
			.collect(Collectors.toList());
		List<Role> savedRoles = roleRepository.saveAll(toSaveRoles);
		allRoles.forEach(role -> {
			for (Role savedRole : savedRoles) {
				if (role.getName().equals(savedRole.getName())) {
					role = savedRole;
				}
			}
		});

		user.setRoles(allRoles);
		return userRepository.save(user);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User findById(String userId) {
		return userRepository.findById(userId);
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

	@HystrixCommand(fallbackMethod = "getDefaultUser")
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(),
			user.getPassword(),
			mapRolesToAuthorities(user.getRoles()));
	}

	public UserDetails getDefaultUser(String email) {
		User user = new User();
		user.setEmail("user");
		user.setFirstName("user");
		user.setLastName("");
		user.setPassword(bCryptPasswordEncoder.encode("password"));
		user.setRoles(Collections.singletonList(new Role("ROLE_DEFAULT")));

		return new org.springframework.security.core.userdetails.User(user.getEmail(),
			user.getPassword(),
			mapRolesToAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream()
			.map(role -> new SimpleGrantedAuthority(role.getName()))
			.collect(Collectors.toList());
	}
}
