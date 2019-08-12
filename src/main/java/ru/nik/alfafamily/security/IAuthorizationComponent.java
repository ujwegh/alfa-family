package ru.nik.alfafamily.security;

import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

public interface IAuthorizationComponent {

	boolean mayGetAccess(@NonNull UserDetails principal, @NonNull String userId);

}
