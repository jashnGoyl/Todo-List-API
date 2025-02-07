package com.jashngoyl.todolist.todolist_api.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.jashngoyl.todolist.todolist_api.constant.ErrorCodeEnum;
import com.jashngoyl.todolist.todolist_api.entity.Todo;
import com.jashngoyl.todolist.todolist_api.entity.User;
import com.jashngoyl.todolist.todolist_api.exception.CustomException;
import com.jashngoyl.todolist.todolist_api.repository.ToDoRepository;
import com.jashngoyl.todolist.todolist_api.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ToDoServiceHelper {

    private UserRepository userRepository;

    private ToDoRepository toDoRepository;

    public ToDoServiceHelper(UserRepository userRepository, ToDoRepository toDoRepository) {
        this.userRepository = userRepository;
        this.toDoRepository = toDoRepository;
    }

    public UserDetails authenticateUserWithUserDetails() {
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

    public Todo getExistingToDo(Long id) {
        Todo existingToDo = toDoRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        ErrorCodeEnum.TODO_NOT_FOUND.getErrorCode(),
                        ErrorCodeEnum.TODO_NOT_FOUND.getErrorMessage(),
                        HttpStatus.NOT_FOUND,
                        "Not Found"));
        log.info("Existing todo: " + existingToDo);
        return existingToDo;
    }

    public User getAuthenticatedUser(String authenticatedUsername) {
        User authenticatedUser = userRepository.findByEmail(authenticatedUsername)
                .orElseThrow(() -> new CustomException(
                        ErrorCodeEnum.USER_NOT_FOUND.getErrorCode(),
                        ErrorCodeEnum.USER_NOT_FOUND.getErrorMessage(),
                        HttpStatus.UNAUTHORIZED,
                        "Unauthorized."));
        log.info("Authenticated User: " + authenticatedUser);
        return authenticatedUser;
    }

    public boolean checkAuthorization(Todo existingToDo, User authenticatedUser, Long id) {
        if (!existingToDo.getUser().getId().equals(authenticatedUser.getId())) {
            log.error("Unauthorized user. Throwing custom exception");
            throw new CustomException(ErrorCodeEnum.UNAUTHORIZED_ACTION.getErrorCode(),
                    ErrorCodeEnum.UNAUTHORIZED_ACTION.getErrorMessage(),
                    HttpStatus.FORBIDDEN,
                    "Action not allowed for user with id: " + id);
        }
        return true;
    }
}
