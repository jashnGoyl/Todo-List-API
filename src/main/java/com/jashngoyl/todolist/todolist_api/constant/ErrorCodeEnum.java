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

    // JWT Specific Errors
    TOKEN_EXPIRED(1007, "JWT token has expired"),
    INVALID_SIGNATURE(1008, "Invalid JWT signature"),
    INVALID_TOKEN(1009, "Malformed or unsupported JWT token"),
    TOKEN_MISSING(1010, "JWT token is missing or empty");

    private final int errorCode;
    private final String errorMessage;

    ErrorCodeEnum(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
