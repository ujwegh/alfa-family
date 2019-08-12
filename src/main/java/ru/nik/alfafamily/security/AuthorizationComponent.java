package ru.nik.alfafamily.security;

import javax.annotation.Nonnull;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.service.UserService;

@Component("auth")
public class AuthorizationComponent implements IAuthorizationComponent {

	private final UserService service;

	@Autowired
	public AuthorizationComponent(UserService service) {
		this.service = service;
	}

	@Override
	public boolean mayGetAccess(@Nonnull final UserDetails principal, @Nonnull @NonNull String userId) {
		User user = service.findByEmail(principal.getUsername());
		return user.getId().equals(userId);
	}
}
