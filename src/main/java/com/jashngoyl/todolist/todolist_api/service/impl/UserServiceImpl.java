package com.jashngoyl.todolist.todolist_api.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jashngoyl.todolist.todolist_api.constant.ErrorCodeEnum;
import com.jashngoyl.todolist.todolist_api.dto.AuthenticationRequestDTO;
import com.jashngoyl.todolist.todolist_api.dto.AuthenticationResponseDTO;
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

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthenticationResponseDTO registerUser(AuthenticationRequestDTO authenticationRequestDTO) {
        log.info("Entered the User service layer. Inside registerUser method.");
        
        if (userRepository.findByEmail(authenticationRequestDTO.getEmail()).isPresent()) {
            log.error("Email already in use!!");
            throw new CustomException(
                ErrorCodeEnum.EMAIL_ALREADY_EXISTS.getErrorCode(),
                ErrorCodeEnum.EMAIL_ALREADY_EXISTS.getErrorMessage(),
                HttpStatus.CONFLICT,
                "Attempted to register with an email that already exists in the system."
            );
        }
        
        String hashedPassword = passwordEncoder.encode(authenticationRequestDTO.getPassword());

        User user = User.builder()
                .email(authenticationRequestDTO.getEmail())
                .name(authenticationRequestDTO.getName())
                .password(hashedPassword)
                .build();
        
        log.info("Built the User for UserEntity: "+user);
        userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getEmail());
        log.info("Token: "+token);

        return AuthenticationResponseDTO.builder().token(token).build();
    }

}
