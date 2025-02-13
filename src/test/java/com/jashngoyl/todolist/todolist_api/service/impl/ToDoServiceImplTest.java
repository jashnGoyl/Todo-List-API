package com.jashngoyl.todolist.todolist_api.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

import com.jashngoyl.todolist.todolist_api.constant.ErrorCodeEnum;
import com.jashngoyl.todolist.todolist_api.dto.GetTodosResponseDTO;
import com.jashngoyl.todolist.todolist_api.dto.ToDoRequestDTO;
import com.jashngoyl.todolist.todolist_api.dto.ToDoResponseDTO;
import com.jashngoyl.todolist.todolist_api.entity.Todo;
import com.jashngoyl.todolist.todolist_api.entity.User;
import com.jashngoyl.todolist.todolist_api.exception.CustomException;
import com.jashngoyl.todolist.todolist_api.repository.ToDoRepository;
import com.jashngoyl.todolist.todolist_api.service.ToDoServiceHelper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class ToDoServiceImplTest {

    @Mock
    private ToDoRepository toDoRepository;

    @Mock
    private ToDoServiceHelper toDoServiceHelper;

    @InjectMocks
    private ToDoServiceImpl toDoService;

    private User user;
    private Todo todo;
    private ToDoRequestDTO toDoRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Test Todo");
        todo.setDescription("Test Description");
        todo.setUser(user);

        toDoRequestDTO = new ToDoRequestDTO("Test Todo", "Test Description");
    }

    @Test
    void createToDo_Success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(toDoServiceHelper.authenticateUserWithUserDetails()).thenReturn(userDetails);
        when(toDoServiceHelper.getAuthenticatedUser(user.getEmail())).thenReturn(user);
        when(toDoRepository.save(any(Todo.class))).thenReturn(todo);

        ToDoResponseDTO response = toDoService.createToDO(toDoRequestDTO);
        assertNotNull(response);
        assertEquals(todo.getId(), response.getId());
        assertEquals(todo.getTitle(), response.getTitle());

        log.info("createToDo_Success Passed");
    }

    @Test
    void updateToDo_Success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(toDoServiceHelper.authenticateUserWithUserDetails()).thenReturn(userDetails);
        when(toDoServiceHelper.getExistingToDo(todo.getId())).thenReturn(todo);
        when(toDoServiceHelper.getAuthenticatedUser(user.getEmail())).thenReturn(user);
        when(toDoRepository.save(any(Todo.class))).thenReturn(todo);

        ToDoResponseDTO response = toDoService.updateToDo(todo.getId(), toDoRequestDTO);
        assertNotNull(response);
        assertEquals(todo.getTitle(), response.getTitle());
    }

    @Test
    void updateToDo_NotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(toDoServiceHelper.authenticateUserWithUserDetails()).thenReturn(userDetails);
        when(toDoServiceHelper.getExistingToDo(todo.getId())).thenThrow(new CustomException(
                ErrorCodeEnum.TODO_NOT_FOUND.getErrorCode(),
                ErrorCodeEnum.TODO_NOT_FOUND.getErrorMessage(),
                HttpStatus.NOT_FOUND,
                "Not Found"));
        CustomException customException = assertThrows(CustomException.class,
                () -> toDoService.updateToDo(todo.getId(), toDoRequestDTO));
        assertEquals(ErrorCodeEnum.TODO_NOT_FOUND.getErrorCode(), customException.getErrorCode());
    }

    @Test
    void updateToDo_NotAuthorized() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@example.com");
        todo.setUser(anotherUser);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(toDoServiceHelper.authenticateUserWithUserDetails()).thenReturn(userDetails);
        when(toDoServiceHelper.getExistingToDo(todo.getId())).thenReturn(todo);
        when(toDoServiceHelper.getAuthenticatedUser(user.getEmail())).thenReturn(user);
        doThrow(new CustomException(ErrorCodeEnum.UNAUTHORIZED_ACTION.getErrorCode(),
                ErrorCodeEnum.UNAUTHORIZED_ACTION.getErrorMessage(),
                HttpStatus.FORBIDDEN,
                "Action not allowed for user with id: " + todo.getId()))
                .when(toDoServiceHelper).checkAuthorization(todo, user, todo.getId());

        CustomException customException = assertThrows(CustomException.class,
                () -> toDoService.updateToDo(todo.getId(), toDoRequestDTO));
        System.out.println(customException);
        assertEquals(ErrorCodeEnum.UNAUTHORIZED_ACTION.getErrorCode(), customException.getErrorCode());
    }

    @Test
    void deleteToDo_Success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(toDoServiceHelper.authenticateUserWithUserDetails()).thenReturn(userDetails);
        when(toDoServiceHelper.getExistingToDo(todo.getId())).thenReturn(todo);
        when(toDoServiceHelper.getAuthenticatedUser(user.getEmail())).thenReturn(user);

        assertDoesNotThrow(() -> toDoService.deleteToDo(todo.getId()));
        verify(toDoRepository, times(1)).delete(todo);
    }

    @Test
    void deleteToDo_NotAuthorized() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@example.com");
        todo.setUser(anotherUser);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(toDoServiceHelper.authenticateUserWithUserDetails()).thenReturn(userDetails);
        when(toDoServiceHelper.getExistingToDo(todo.getId())).thenReturn(todo);
        when(toDoServiceHelper.getAuthenticatedUser(user.getEmail())).thenReturn(user);
        doThrow(new CustomException(ErrorCodeEnum.UNAUTHORIZED_ACTION.getErrorCode(),
                ErrorCodeEnum.UNAUTHORIZED_ACTION.getErrorMessage(),
                HttpStatus.FORBIDDEN,
                "Action not allowed for user with id: " + todo.getId()))
                .when(toDoServiceHelper).checkAuthorization(todo, user, todo.getId());

        CustomException customException = assertThrows(CustomException.class,
                () -> toDoService.deleteToDo(todo.getId()));
        assertEquals(ErrorCodeEnum.UNAUTHORIZED_ACTION.getErrorCode(), customException.getErrorCode());
    }

    @Test
    void getUserTodos_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        Page<Todo> page = new PageImpl<>(Collections.singletonList(todo));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(toDoServiceHelper.authenticateUserWithUserDetails()).thenReturn(userDetails);
        when(toDoServiceHelper.getAuthenticatedUser(user.getEmail())).thenReturn(user);
        when(toDoRepository.findByUserAndTitleContainingIgnoreCase(eq(user), eq(""), eq(pageable))).thenReturn(page);

        GetTodosResponseDTO response = toDoService.getUserTodos(0, 10, "title", "asc", "");
        assertNotNull(response);
        assertEquals(1, response.getTotal());
    }

}
