package ru.nik.alfafamily.config.migration;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.nik.alfafamily.domain.Role;
import ru.nik.alfafamily.domain.User;

@ChangeLog
public class DatabaseChangelog {

	@ChangeSet(order = "001", id = "roleChangeSet", author = "nik")
	public void rolesInit(MongoTemplate mongoTemplate){

		Role admin = new Role("ROLE_ADMIN");
		Role user = new Role("ROLE_USER");

		mongoTemplate.save(admin);
		mongoTemplate.save(user);
	}

	@ChangeSet(order = "002", id = "userChangeSet", author = "nik")
	public void userInit(MongoTemplate mongoTemplate) {
		User user = new User();
		user.setEmail("admin@gmail.com");
		user.setPassword("password");
		user.setFirstName("admin");
		user.setLastName("admin");
		user.setEnabled(true);

		List<Role> role = mongoTemplate.findAll(Role.class);
		Role admin = new Role("ROLE_ADMIN");

		for (Role r : role) {
			if (r.getName().equals("ROLE_ADMIN")) {
				admin = r;
			}
		}
		user.setRole(admin);

		mongoTemplate.save(user);
	}

}
