package com.jashngoyl.todolist.todolist_api.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.jashngoyl.todolist.todolist_api.constant.ErrorCodeEnum;
import com.jashngoyl.todolist.todolist_api.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ToDoServiceHelper {

    public UserDetails authenticateUserWithUserDetails(){
         Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.info("principal: " + principal);
            log.error("UnAuthorized");
            throw new CustomException(
                    ErrorCodeEnum.UNAUTHORIZED.getErrorCode(),
                    ErrorCodeEnum.UNAUTHORIZED.getErrorMessage(),
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized.");
        }
        return userDetails;
    }
}
