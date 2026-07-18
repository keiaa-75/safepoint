package com.keiaa.safepoint.exception;

public class DuplicateAdminUsernameException extends RuntimeException {
    public DuplicateAdminUsernameException(String message) {
        super(message);
    }
}
