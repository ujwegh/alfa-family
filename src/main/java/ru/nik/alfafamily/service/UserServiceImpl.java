package ru.nik.alfafamily.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.dto.UserDto;
import ru.nik.alfafamily.dto.UserRegistrationDto;
import ru.nik.alfafamily.repository.RoleRepository;
import ru.nik.alfafamily.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
		BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
		List<Role> roleList = new ArrayList<>();
		String[] roles = userDto.getRoles().split(", ");

		List<Role> existedRoles = roleRepository.findAllByNameIn(roles);

		for (String roleName : roles) {
			existedRoles.forEach(role -> roleList.add(roleName.equals(role.getName()) ? role : new Role(roleName)));
		}

		User user = userRepository.findByEmail(userDto.getEmail());
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setEmail(userDto.getEmail());
		user.setEnabled(userDto.isEnabled());
		user.setPassword(userDto.getPassword());
		user.setRoles(roleList);

		return userRepository.save(user);
	}

	@Override
	public User findByEmail(String email){
		return userRepository.findByEmail(email);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		if (user == null){
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(),
			user.getPassword(),
			mapRolesToAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles){
		return roles.stream()
			.map(role -> new SimpleGrantedAuthority(role.getName()))
			.collect(Collectors.toList());
	}
}
