package com.jashngoyl.todolist.todolist_api.service.impl;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.jashngoyl.todolist.todolist_api.constant.ErrorCodeEnum;
import com.jashngoyl.todolist.todolist_api.entity.User;
import com.jashngoyl.todolist.todolist_api.exception.CustomException;
import com.jashngoyl.todolist.todolist_api.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    private UserRepository userRepository;

    CustomUserDetailService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("Inside UserInfoService.");
        
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(
                    ErrorCodeEnum.USER_NOT_FOUND.getErrorCode(),
                    ErrorCodeEnum.USER_NOT_FOUND.getErrorMessage(),
                    HttpStatus.NOT_FOUND,
                    "User with email '" + username + "' was not found in the database."
                )
            );

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            new ArrayList<>()
        );
        log.info("Converted the User to org.springframework.security.core.userdetails.User object: "+userDetails);
        return userDetails;

    }

}
