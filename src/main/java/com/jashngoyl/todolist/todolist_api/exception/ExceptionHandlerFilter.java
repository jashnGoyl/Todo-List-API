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
        }catch (CustomException ex) {
            log.info("Inside Exception Handler Filter and handling CustomException!!!");
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(ex.getErrorCode())
                    .errorMessage(ex.getMessage())
                    .statusCode(ex.getStatusCode())
                    .httpMethod(request.getMethod())
                    .backendMessage(ex.getBackendMessage())
                    .timestamp(LocalDateTime.now())
                    .details(null)
                    .build();

            log.info("ErrorResponse for CustomException: " + errorResponse);

            response.setStatus(ex.getStatusCode());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            response.getWriter().flush();
        } catch (DataIntegrityViolationException ex) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(DatabaseErrorCodeEnum.CONSTRAINT_VIOLATION.getErrorCode())
                    .errorMessage(DatabaseErrorCodeEnum.CONSTRAINT_VIOLATION.getErrorMessage())
                    .backendMessage(ex.getMessage())
                    .httpMethod(request.getMethod())
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now())
                    .build();

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            response.getWriter().flush();
        } catch (CannotCreateTransactionException ex) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(DatabaseErrorCodeEnum.CONNECTION_FAILURE.getErrorCode())
                    .errorMessage(DatabaseErrorCodeEnum.CONNECTION_FAILURE.getErrorMessage())
                    .backendMessage(ex.getMessage())
                    .httpMethod(request.getMethod())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .timestamp(LocalDateTime.now())
                    .build();

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            response.getWriter().flush();
        } catch (Exception e) {
            log.info("Inside Exception Handler Filter!!!");
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(ErrorCodeEnum.GENERIC_ERROR.getErrorCode())
                    .errorMessage(ErrorCodeEnum.GENERIC_ERROR.getErrorMessage())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .httpMethod(request.getMethod())
                    .backendMessage(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .details(null)
                    .build();

            log.info("Error Response for Exception: " + errorResponse);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            response.getWriter().flush();
        }
    }

}
