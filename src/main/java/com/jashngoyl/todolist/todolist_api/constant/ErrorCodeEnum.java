package com.jashngoyl.todolist.todolist_api.constant;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ErrorCodeEnum {

    EMAIL_ALREADY_EXISTS(1001, "Email already exists"),
    USER_NOT_FOUND(1002, "User not found"),
    GENERIC_ERROR(1000, "An unexpected error occurred"),
    VALIDATION_ERROR(10004, "Validation failed"),
    INVALID_CREDENTIALS(10004, "Invalid email or password"),
    AUTHENTICATION_FAILURE(10005, "Authentication failed"),
    UNAUTHORIZED(10006, "Unauthorized access. Please provide a valid token."),

    TODO_NOT_FOUND(10007, "To-do item not found"),
    UNAUTHORIZED_ACTION(10008, "You are not authorized to perform any action on this to-do item"),

    // JWT Specific Errors
    TOKEN_EXPIRED(2001, "JWT token has expired"),
    INVALID_SIGNATURE(2002, "Invalid JWT signature"),
    INVALID_TOKEN(2003, "Malformed or unsupported JWT token"),
    TOKEN_MISSING(2004, "JWT token is missing or empty");

    private final int errorCode;
    private final String errorMessage;

    ErrorCodeEnum(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
