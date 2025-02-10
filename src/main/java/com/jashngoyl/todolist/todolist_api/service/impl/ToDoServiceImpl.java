package com.jashngoyl.todolist.todolist_api.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.jashngoyl.todolist.todolist_api.dto.GetTodosResponseDTO;
import com.jashngoyl.todolist.todolist_api.dto.ToDoRequestDTO;
import com.jashngoyl.todolist.todolist_api.dto.ToDoResponseDTO;
import com.jashngoyl.todolist.todolist_api.entity.Todo;
import com.jashngoyl.todolist.todolist_api.entity.User;
import com.jashngoyl.todolist.todolist_api.repository.ToDoRepository;
import com.jashngoyl.todolist.todolist_api.service.ToDoServiceHelper;
import com.jashngoyl.todolist.todolist_api.service.interfaces.ToDoServiceInterface;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ToDoServiceImpl implements ToDoServiceInterface {

        private ToDoRepository toDoRepository;

        private ToDoServiceHelper toDoServiceHelper;

        public ToDoServiceImpl(ToDoRepository toDoRepository,
                        ToDoServiceHelper toDoServiceHelper) {
                this.toDoRepository = toDoRepository;
                this.toDoServiceHelper = toDoServiceHelper;
        }

        @Override
        public ToDoResponseDTO createToDO(ToDoRequestDTO toDoRequestDTO) {
                log.info("recieved request in service layer: " + toDoRequestDTO);

                UserDetails userDetails = toDoServiceHelper.authenticateUserWithUserDetails();
                log.info("Recieved user details from helper: " + userDetails);

                String authenticatedUsername = userDetails.getUsername();
                log.info("Email from UserDetails: " + authenticatedUsername);

                User authenticatedUser = toDoServiceHelper.getAuthenticatedUser(authenticatedUsername);
                log.info("Authenticated User: " + authenticatedUser);

                Todo todo = Todo.builder()
                                .title(toDoRequestDTO.getTitle())
                                .description(toDoRequestDTO.getDescription())
                                .user(authenticatedUser)
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
                log.info("Recieved DTO request from controller: " + toDoRequestDTO);

                UserDetails userDetails = toDoServiceHelper.authenticateUserWithUserDetails();
                log.info("Recieved user details from helper: " + userDetails);

                String authenticatedUsername = userDetails.getUsername();
                log.info("authenticated username: " + authenticatedUsername);

                Todo existingToDo = toDoServiceHelper.getExistingToDo(id);
                log.info("Existing todo: " + existingToDo);

                User authenticatedUser = toDoServiceHelper.getAuthenticatedUser(authenticatedUsername);
                log.info("Authenticated User: " + authenticatedUser);

                toDoServiceHelper.checkAuthorization(existingToDo, authenticatedUser, id);

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

        @Override
        public void deleteToDo(Long id) {
                log.info("Received id in service layer to delete: " + id);

                UserDetails userDetails = toDoServiceHelper.authenticateUserWithUserDetails();
                log.info("Recieved user details from helper: " + userDetails);

                String authenticatedUsername = userDetails.getUsername();
                log.info("authenticated username: " + authenticatedUsername);

                Todo existingToDo = toDoServiceHelper.getExistingToDo(id);
                log.info("Existing todo: " + existingToDo);

                User authenticatedUser = toDoServiceHelper.getAuthenticatedUser(authenticatedUsername);
                log.info("Authenticated User: " + authenticatedUser);

                toDoServiceHelper.checkAuthorization(existingToDo, authenticatedUser, id);

                toDoRepository.delete(existingToDo);
                log.info("Deleted todo: " + existingToDo);
        }

        @Override
        public GetTodosResponseDTO getUserTodos(int page, int limit, String sortBy, String direction,
                        String titleFilter) {

                UserDetails userDetails = toDoServiceHelper.authenticateUserWithUserDetails();
                log.info("Recieved user details from helper: " + userDetails);

                String authenticatedUsername = userDetails.getUsername();
                log.info("authenticated username: " + authenticatedUsername);

                User authenticatedUser = toDoServiceHelper.getAuthenticatedUser(authenticatedUsername);
                log.info("Authenticated User: " + authenticatedUser);

                Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC
                                : Sort.Direction.ASC;
                log.info("Sort Direction: " + sortDirection);

                Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortBy));
                log.info("Pageable: " + pageable);

                Page<Todo> todosPage = toDoRepository.findByUserAndTitleContainingIgnoreCase(authenticatedUser,
                                titleFilter, pageable);
                log.info("recieved page: " + todosPage);

                GetTodosResponseDTO getTodosResponseDTO = GetTodosResponseDTO.builder()
                                .data(todosPage.getContent())
                                .page(todosPage.getNumber())
                                .limit(todosPage.getSize())
                                .total((int) todosPage.getNumberOfElements())
                                .build();
                log.info("GetResponseDTO: " + getTodosResponseDTO);

                return getTodosResponseDTO;
        }
}
