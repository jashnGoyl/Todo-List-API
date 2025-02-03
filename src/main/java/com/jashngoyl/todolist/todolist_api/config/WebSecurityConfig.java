package com.jashngoyl.todolist.todolist_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.jashngoyl.todolist.todolist_api.exception.ExceptionHandlerFilter;
import com.jashngoyl.todolist.todolist_api.security.JwtRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig {

    private JwtRequestFilter jwtRequestFilter;

    private ExceptionHandlerFilter exceptionHandlerFilter;

    public WebSecurityConfig(JwtRequestFilter jwtRequestFilter, ExceptionHandlerFilter exceptionHandlerFilter){
        this.jwtRequestFilter = jwtRequestFilter;
        this.exceptionHandlerFilter = exceptionHandlerFilter;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        log.info("Inside AuthenticationManager method");
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf -> csrf.disable()) // Disable CSRF for stateless JWT authentication
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("todo-list/auth/register","todo-list/auth/login").permitAll() // Public endpoints
            .anyRequest().authenticated() // Secure other endpoints
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless sessions for JWT
        );
        http.addFilterBefore(exceptionHandlerFilter, AbstractPreAuthenticatedProcessingFilter.class);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

        log.info("Implemented SecurityFilterChain method");
        return http.build();
    }
}
