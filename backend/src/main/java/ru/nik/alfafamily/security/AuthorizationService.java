package ru.nik.alfafamily.security;

import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthorizationService {

	boolean mayGetAccess(@NonNull UserDetails principal, @NonNull String userId);

}
