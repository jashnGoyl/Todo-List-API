package com.jashngoyl.todolist.todolist_api.service.interfaces;

import com.jashngoyl.todolist.todolist_api.dto.AuthenticationRequestDTO;
import com.jashngoyl.todolist.todolist_api.dto.AuthenticationResponseDTO;

public interface UserServiceInterface {
    
    public AuthenticationResponseDTO registerUser(AuthenticationRequestDTO authenticationRequestDTO);
}
