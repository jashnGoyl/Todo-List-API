package com.jashngoyl.todolist.todolist_api.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jashngoyl.todolist.todolist_api.constant.ErrorCodeEnum;
import com.jashngoyl.todolist.todolist_api.exception.CustomException;
import com.jashngoyl.todolist.todolist_api.service.impl.CustomUserDetailService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    // Validates the JWT token and set the SecurityContext with User details making
    // the user logging in
    private JwtUtil jwtUtil;

    private CustomUserDetailService userDetailServiceImpl;

    JwtRequestFilter(JwtUtil jwtUtil, CustomUserDetailService userDetailServiceImpl) {
        this.jwtUtil = jwtUtil;
        this.userDetailServiceImpl = userDetailServiceImpl;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        try {
            log.info("Inside JWT filter and doFilterInteral method");
            final String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader == null && !(request.getRequestURI().endsWith("login")
                    || request.getRequestURI().endsWith("register"))) {
                throw new IllegalArgumentException();
            }

            String username = null;
            String jwt = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            chain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            log.error("Throwing ExpiredJwtException.");
            throw new CustomException(
                    ErrorCodeEnum.TOKEN_EXPIRED.getErrorCode(), ErrorCodeEnum.TOKEN_EXPIRED.getErrorMessage(),
                    HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (@SuppressWarnings("deprecation") SignatureException ex) {
            log.error("Throwing SignatureException.");
            throw new CustomException(
                    ErrorCodeEnum.INVALID_SIGNATURE.getErrorCode(), ErrorCodeEnum.INVALID_SIGNATURE.getErrorMessage(),
                    HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (MalformedJwtException | UnsupportedJwtException ex) {
            log.error("Throwing MalformedJwtException | UnsupportedJwtException.");
            throw new CustomException(
                    ErrorCodeEnum.INVALID_TOKEN.getErrorCode(), ErrorCodeEnum.INVALID_TOKEN.getErrorMessage(),
                    HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Throwing IllegalArgumentException.");
            throw new CustomException(
                    ErrorCodeEnum.TOKEN_MISSING.getErrorCode(), ErrorCodeEnum.TOKEN_MISSING.getErrorMessage(),
                    HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (Exception ex) {
            log.error("Throwing Exception.");
            throw new CustomException(
                    ErrorCodeEnum.GENERIC_ERROR.getErrorCode(), ErrorCodeEnum.GENERIC_ERROR.getErrorMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}
