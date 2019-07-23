package ru.nik.alfafamily.exceptions;

public class FamilyMemberDoesNotExistsException extends RuntimeException {

	public FamilyMemberDoesNotExistsException(String message) {
		super(message);
	}
}
