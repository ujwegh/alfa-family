package ru.nik.alfafamily.security;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.nik.alfafamily.domain.User;
import ru.nik.alfafamily.service.UserService;

@Service("auth")
public class AuthorizationServiceImpl implements AuthorizationService {

	@Autowired
	private UserService service;

	@HystrixCommand(fallbackMethod = "error")
	@Override
	public boolean mayGetAccess(@Nonnull final UserDetails principal, @Nonnull final String userId) {
		User user = service.findByEmail(principal.getUsername());
		return user.getId().equals(userId);
	}

	public boolean error(@Nonnull final UserDetails principal, @Nonnull final String userId){
		return false;
	}
}
