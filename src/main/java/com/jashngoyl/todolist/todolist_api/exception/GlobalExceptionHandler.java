package com.jashngoyl.todolist.todolist_api.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.jashngoyl.todolist.todolist_api.constant.DatabaseErrorCodeEnum;
import com.jashngoyl.todolist.todolist_api.constant.ErrorCodeEnum;
import com.jashngoyl.todolist.todolist_api.pojo.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
                        HttpServletRequest httpServletRequest) {
                List<String> errors = ex.getBindingResult().getFieldErrors()
                                .stream()
                                .map(FieldError::getDefaultMessage)
                                .toList();

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .errorCode(ErrorCodeEnum.VALIDATION_ERROR.getErrorCode())
                                .errorMessage(ErrorCodeEnum.VALIDATION_ERROR.getErrorMessage())
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .httpMethod(httpServletRequest.getMethod())
                                .timestamp(LocalDateTime.now())
                                .backendMessage(ex.getMessage())
                                .details(errors)
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(CustomException.class)
        public ResponseEntity<ErrorResponse> handleCustomExceptions(CustomException ex,
                        HttpServletRequest httpServletRequest) {

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .errorCode(ex.getErrorCode())
                                .errorMessage(ex.getMessage())
                                .statusCode(ex.getStatusCode())
                                .httpMethod(httpServletRequest.getMethod())
                                .timestamp(LocalDateTime.now())
                                .backendMessage(ex.getBackendMessage())
                                .details(null)
                                .build();

                return new ResponseEntity<>(errorResponse, ex.getStatus());
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> handleConstraintViolation(DataIntegrityViolationException ex,
                        HttpServletRequest httpServletRequest) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .errorCode(DatabaseErrorCodeEnum.CONSTRAINT_VIOLATION.getErrorCode())
                                .errorMessage(DatabaseErrorCodeEnum.CONSTRAINT_VIOLATION.getErrorMessage())
                                .backendMessage(ex.getMostSpecificCause().getMessage())
                                .httpMethod(httpServletRequest.getMethod())
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(CannotCreateTransactionException.class)
        public ResponseEntity<ErrorResponse> handleConnectionFailure(CannotCreateTransactionException ex,
                        HttpServletRequest httpServletRequest) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .errorCode(DatabaseErrorCodeEnum.CONNECTION_FAILURE.getErrorCode())
                                .errorMessage(DatabaseErrorCodeEnum.CONNECTION_FAILURE.getErrorMessage())
                                .backendMessage(ex.getMessage())
                                .httpMethod(httpServletRequest.getMethod())
                                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
