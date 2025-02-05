package com.jashngoyl.todolist.todolist_api.exception;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jashngoyl.todolist.todolist_api.constant.DatabaseErrorCodeEnum;
import com.jashngoyl.todolist.todolist_api.constant.ErrorCodeEnum;
import com.jashngoyl.todolist.todolist_api.pojo.ErrorResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private ObjectMapper objectMapper;

    public ExceptionHandlerFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("Request Path: " + request.getRequestURI()); // Log request path
        try {
            filterChain.doFilter(request, response);
            log.info("Exiting ExceptionHandlerFilter for request: {}", request.getRequestURI());
        } catch (CustomException ex) {
            log.info("Inside Exception Handler Filter and handling CustomException!!!");
            handleException(response, request, ex.getErrorCode(), ex.getMessage(), ex.getStatusCode(), ex.getBackendMessage());
        } catch (DataIntegrityViolationException ex) {
            handleException(response, request, DatabaseErrorCodeEnum.CONSTRAINT_VIOLATION.getErrorCode(),
                    DatabaseErrorCodeEnum.CONSTRAINT_VIOLATION.getErrorMessage(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        } catch (CannotCreateTransactionException ex) {
            handleException(response, request, DatabaseErrorCodeEnum.CONNECTION_FAILURE.getErrorCode(),
            DatabaseErrorCodeEnum.CONNECTION_FAILURE.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        } catch (Exception ex) {
            log.info("Inside Exception Handler Filter!!!");
            handleException(response, request, ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),
            ErrorCodeEnum.GENERIC_ERROR.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        }
    }

    private void handleException(HttpServletResponse response, HttpServletRequest request, int errorCode,
            String errorMessage, int statusCode, String backendMessage)
            throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .statusCode(statusCode)
                .httpMethod(request.getMethod())
                .backendMessage(backendMessage)
                .timestamp(LocalDateTime.now())
                .details(null)
                .build();

        log.info("Error Response: " + errorResponse);
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }

}
