package ru.nik.alfafamily.exceptions;

public class CategoryDoesntExistsException extends RuntimeException {

	public CategoryDoesntExistsException(String message) {
		super(message);
	}
}
