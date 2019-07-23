package ru.nik.alfafamily.exceptions;

public class UserDoesNotExistsException extends RuntimeException {

	public UserDoesNotExistsException(String message) {
		super(message);
	}
}
