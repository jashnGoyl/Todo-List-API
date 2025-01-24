package com.jashngoyl.todolist.todolist_api.constant;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ErrorCodeEnum {

    EMAIL_ALREADY_EXISTS(1001, "Email already exists"),
    USER_NOT_FOUND(1002, "User not found"),
    GENERIC_ERROR(1000, "An unexpected error occurred"),
    VALIDATION_ERROR(10004, "Validation failed");

    private final int errorCode;
    private final String errorMessage;

    ErrorCodeEnum(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
