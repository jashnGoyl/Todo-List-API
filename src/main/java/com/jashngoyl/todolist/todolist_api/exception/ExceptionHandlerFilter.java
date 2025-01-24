package com.jashngoyl.todolist.todolist_api.exception;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.Gson;
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

    private Gson gson;

    public ExceptionHandlerFilter(Gson gson) {
        this.gson = gson;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException ex) {
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
            response.getWriter().write(gson.toJson(errorResponse));
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
            response.getWriter().write(gson.toJson(errorResponse));
            response.getWriter().flush();
        }
    }

}
