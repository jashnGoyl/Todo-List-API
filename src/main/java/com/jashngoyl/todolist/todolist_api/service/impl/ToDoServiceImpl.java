package com.jashngoyl.todolist.todolist_api.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.jashngoyl.todolist.todolist_api.constant.ErrorCodeEnum;
import com.jashngoyl.todolist.todolist_api.dto.ToDoRequestDTO;
import com.jashngoyl.todolist.todolist_api.dto.ToDoResponseDTO;
import com.jashngoyl.todolist.todolist_api.entity.Todo;
import com.jashngoyl.todolist.todolist_api.entity.User;
import com.jashngoyl.todolist.todolist_api.exception.CustomException;
import com.jashngoyl.todolist.todolist_api.repository.ToDoRepository;
import com.jashngoyl.todolist.todolist_api.repository.UserRepository;
import com.jashngoyl.todolist.todolist_api.service.interfaces.ToDoServiceInterface;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ToDoServiceImpl implements ToDoServiceInterface {

    private UserRepository userRepository;

    private ToDoRepository toDoRepository;

    public ToDoServiceImpl(UserRepository userRepository, ToDoRepository toDoRepository) {
        this.userRepository = userRepository;
        this.toDoRepository = toDoRepository;
    }

    @Override
    public ToDoResponseDTO createToDO(ToDoRequestDTO toDoRequestDTO, String authorizationHeader) {
        log.info("recieved request in service layer: " + toDoRequestDTO);

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

        String email = userDetails.getUsername();
        log.info("Email from UserDetails: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        ErrorCodeEnum.USER_NOT_FOUND.getErrorCode(),
                        ErrorCodeEnum.USER_NOT_FOUND.getErrorMessage(),
                        HttpStatus.NOT_FOUND,
                        "User with email '" + email + "' was not found in the database."
                        )
                    );
        log.info("User: " + user.toString());

        Todo todo = Todo.builder()
                .title(toDoRequestDTO.getTitle())
                .description(toDoRequestDTO.getDescription())
                .user(user)
                .build();
        log.info("Todo: "+todo);

        todo = toDoRepository.save(todo);

        ToDoResponseDTO toDoResponseDTO = ToDoResponseDTO.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .descrption(todo.getDescription())
                .build();
        log.info("ToDoResponseDTO: "+toDoResponseDTO);

        return toDoResponseDTO;
    }

}
