package com.jashngoyl.todolist.todolist_api.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jashngoyl.todolist.todolist_api.constant.ErrorCodeEnum;
import com.jashngoyl.todolist.todolist_api.dto.RegisterRequestDTO;
import com.jashngoyl.todolist.todolist_api.dto.AuthenticationResponseDTO;
import com.jashngoyl.todolist.todolist_api.dto.LoginRequestDTO;
import com.jashngoyl.todolist.todolist_api.entity.User;
import com.jashngoyl.todolist.todolist_api.exception.CustomException;
import com.jashngoyl.todolist.todolist_api.repository.UserRepository;
import com.jashngoyl.todolist.todolist_api.security.JwtUtil;
import com.jashngoyl.todolist.todolist_api.service.interfaces.UserServiceInterface;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserServiceInterface {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private JwtUtil jwtUtil;

    private AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
     AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticationResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) {
        log.info("Entered the User service layer. Inside registerUser method.");
        
        if (userRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            log.error("Email already in use!!");
            throw new CustomException(
                ErrorCodeEnum.EMAIL_ALREADY_EXISTS.getErrorCode(),
                ErrorCodeEnum.EMAIL_ALREADY_EXISTS.getErrorMessage(),
                HttpStatus.CONFLICT,
                "Attempted to register with an email that already exists in the system."
            );
        }
        
        String hashedPassword = passwordEncoder.encode(registerRequestDTO.getPassword());

        User user = User.builder()
                .email(registerRequestDTO.getEmail())
                .name(registerRequestDTO.getName())
                .password(hashedPassword)
                .build();
        
        log.info("Built the User for UserEntity: "+user);
        userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getEmail());
        log.info("Token: "+token);

        return AuthenticationResponseDTO.builder().token(token).build();
    }

    @Override
    public AuthenticationResponseDTO loginUser(LoginRequestDTO loginRequestDTO) {
        log.info("Inside LoginUser Service method.");
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
            );

            String token = jwtUtil.generateToken(loginRequestDTO.getEmail());
            log.info("Generated Token: "+ token);

            return AuthenticationResponseDTO.builder().token(token).build();

        }catch (BadCredentialsException ex) {
            throw new CustomException(
                ErrorCodeEnum.INVALID_CREDENTIALS.getErrorCode(),
                ErrorCodeEnum.INVALID_CREDENTIALS.getErrorMessage(),
                HttpStatus.UNAUTHORIZED, 
                ex.getLocalizedMessage()
            );
        }catch (AuthenticationException ex){
            throw new CustomException(
                ErrorCodeEnum.AUTHENTICATION_FAILURE.getErrorCode(),
                ErrorCodeEnum.AUTHENTICATION_FAILURE.getErrorMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
            );
        }
    }
}
