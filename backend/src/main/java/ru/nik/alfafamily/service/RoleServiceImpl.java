package ru.nik.alfafamily.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.repository.RoleRepository;

@Service
public class RoleServiceImpl implements RoleService {


	private final RoleRepository repository;

	@Autowired
	public RoleServiceImpl(RoleRepository repository) {
		this.repository = repository;
	}


	@Override
	public Role create(String name) {
		Role role = new Role(name);
		return repository.save(role);
	}

	@Override
	public Role getByName(String name) {
		return repository.findByName(name);
	}

	@Override
	public Boolean delete(String name) {
		return repository.deleteByName(name) != 0;
	}

	@Override
	public boolean isExistsByName(String name) {
		return repository.existsByName(name);
	}


}
