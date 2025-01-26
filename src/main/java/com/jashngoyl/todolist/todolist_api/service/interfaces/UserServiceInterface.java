package com.jashngoyl.todolist.todolist_api.service.interfaces;

import com.jashngoyl.todolist.todolist_api.dto.RegisterRequestDTO;
import com.jashngoyl.todolist.todolist_api.dto.AuthenticationResponseDTO;
import com.jashngoyl.todolist.todolist_api.dto.LoginRequestDTO;

public interface UserServiceInterface {
    
    public AuthenticationResponseDTO registerUser(RegisterRequestDTO registerRequestDTO);

    public AuthenticationResponseDTO loginUser(LoginRequestDTO loginRequestDTO);
}
