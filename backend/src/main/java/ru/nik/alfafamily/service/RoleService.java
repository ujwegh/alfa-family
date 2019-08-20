package ru.nik.alfafamily.service;

import ru.nik.alfafamily.domain.Role;

public interface RoleService {

	Role create(String name);

	Role getByName(String name);

	Boolean delete(String name);

	boolean isExistsByName(String name);
}
