package com.jashngoyl.todolist.todolist_api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jashngoyl.todolist.todolist_api.dto.AuthenticationResponseDTO;
import com.jashngoyl.todolist.todolist_api.dto.LoginRequestDTO;
import com.jashngoyl.todolist.todolist_api.dto.RegisterRequestDTO;
import com.jashngoyl.todolist.todolist_api.entity.Todo;
import com.jashngoyl.todolist.todolist_api.entity.User;
import com.jashngoyl.todolist.todolist_api.exception.CustomException;
import com.jashngoyl.todolist.todolist_api.repository.ToDoRepository;
import com.jashngoyl.todolist.todolist_api.repository.UserRepository;
import com.jashngoyl.todolist.todolist_api.security.JwtUtil;
import com.jashngoyl.todolist.todolist_api.service.ToDoServiceHelper;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private ToDoRepository toDoRepository;

    @Mock
    private ToDoServiceHelper toDoServiceHelper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private ToDoServiceImpl toDoService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Todo todo;
    private RegisterRequestDTO registerRequestDTO;
    private LoginRequestDTO loginRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Test Todo");
        todo.setDescription("Test Description");
        todo.setUser(user);

        registerRequestDTO = new RegisterRequestDTO("test@example.com", "password", "Test User");
        loginRequestDTO = new LoginRequestDTO("test@example.com", "password");
    }

    @Test
    void registerUser_Success() {
        when(userRepository.findByEmail(registerRequestDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequestDTO.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(eq(registerRequestDTO.getEmail()))).thenReturn("mockToken");

        AuthenticationResponseDTO response = userService.registerUser(registerRequestDTO);
        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        when(userRepository.findByEmail(registerRequestDTO.getEmail())).thenReturn(Optional.of(user));
        assertThrows(CustomException.class, () -> userService.registerUser(registerRequestDTO));
    }

    @Test
    void loginUser_Success() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(jwtUtil.generateToken(loginRequestDTO.getEmail())).thenReturn("mockToken");

        AuthenticationResponseDTO response = userService.loginUser(loginRequestDTO);
        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
    }

    @Test
    void loginUser_InvalidCredentials() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));
        assertThrows(CustomException.class, () -> userService.loginUser(loginRequestDTO));
    }

}
