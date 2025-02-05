package com.jashngoyl.todolist.todolist_api.service.impl;

import org.springframework.http.HttpStatus;
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
import com.jashngoyl.todolist.todolist_api.service.ToDoServiceHelper;
import com.jashngoyl.todolist.todolist_api.service.interfaces.ToDoServiceInterface;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ToDoServiceImpl implements ToDoServiceInterface {

    private UserRepository userRepository;

    private ToDoRepository toDoRepository;

    private ToDoServiceHelper toDoServiceHelper;

    public ToDoServiceImpl(UserRepository userRepository, ToDoRepository toDoRepository,ToDoServiceHelper toDoServiceHelper) {
        this.userRepository = userRepository;
        this.toDoRepository = toDoRepository;
        this.toDoServiceHelper = toDoServiceHelper;
    }

    @Override
    public ToDoResponseDTO createToDO(ToDoRequestDTO toDoRequestDTO) {
        log.info("recieved request in service layer: " + toDoRequestDTO);

        UserDetails userDetails = toDoServiceHelper.authenticateUserWithUserDetails();
        log.info("Recieved user details from helper: "+userDetails);

        String email = userDetails.getUsername();
        log.info("Email from UserDetails: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        ErrorCodeEnum.USER_NOT_FOUND.getErrorCode(),
                        ErrorCodeEnum.USER_NOT_FOUND.getErrorMessage(),
                        HttpStatus.NOT_FOUND,
                        "User with email '" + email + "' was not found in the database."));
        log.info("User: " + user.toString());

        Todo todo = Todo.builder()
                .title(toDoRequestDTO.getTitle())
                .description(toDoRequestDTO.getDescription())
                .user(user)
                .build();
        log.info("Todo: " + todo);

        todo = toDoRepository.save(todo);

        ToDoResponseDTO toDoResponseDTO = ToDoResponseDTO.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .descrption(todo.getDescription())
                .build();
        log.info("ToDoResponseDTO: " + toDoResponseDTO);

        return toDoResponseDTO;
    }

    @Override
    public ToDoResponseDTO updateToDo(Long id, ToDoRequestDTO toDoRequestDTO) {

        log.info("Received id to update: " + id);
        log.info("Recieved DTO request from controller: "+toDoRequestDTO);

        UserDetails userDetails = toDoServiceHelper.authenticateUserWithUserDetails();
        log.info("Recieved user details from helper: "+userDetails);

        String authenticatedUsername = userDetails.getUsername();
        log.info("authenticated username: " + authenticatedUsername);

        Todo existingToDo = toDoRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        ErrorCodeEnum.TODO_NOT_FOUND.getErrorCode(),
                        ErrorCodeEnum.TODO_NOT_FOUND.getErrorMessage(),
                        HttpStatus.NOT_FOUND,
                        "Not Found"));
        log.info("Existing todo: " + existingToDo);

        User authenticatedUser = userRepository.findByEmail(authenticatedUsername)
                .orElseThrow(() -> new CustomException(
                        ErrorCodeEnum.USER_NOT_FOUND.getErrorCode(),
                        ErrorCodeEnum.USER_NOT_FOUND.getErrorMessage(),
                        HttpStatus.UNAUTHORIZED,
                        "Unauthorized."));

        if (!existingToDo.getUser().getId().equals(authenticatedUser.getId())) {
            log.error("Unauthorized user. Throwing custom exception");
            throw new CustomException(ErrorCodeEnum.UNAUTHORIZED_ACTION.getErrorCode(),
                    ErrorCodeEnum.UNAUTHORIZED_ACTION.getErrorMessage(),
                    HttpStatus.FORBIDDEN,
                    "Action not allowed for user with id: " + id);
        }

        existingToDo.setTitle(toDoRequestDTO.getTitle());
        existingToDo.setDescription(toDoRequestDTO.getDescription());
        Todo updatedTodo = toDoRepository.save(existingToDo);
        log.info("Updated todo: " + updatedTodo);

        ToDoResponseDTO toDoResponseDTO = ToDoResponseDTO.builder()
                .id(updatedTodo.getId())
                .title(updatedTodo.getTitle())
                .descrption(updatedTodo.getDescription())
                .build();
        log.info("TodoResponseEntity: " + toDoResponseDTO);

        return toDoResponseDTO;
    }

}
