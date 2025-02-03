package com.jashngoyl.todolist.todolist_api.service.interfaces;

import com.jashngoyl.todolist.todolist_api.dto.ToDoRequestDTO;
import com.jashngoyl.todolist.todolist_api.dto.ToDoResponseDTO;

public interface ToDoServiceInterface {

    public ToDoResponseDTO createToDO(ToDoRequestDTO toDoRequestDTO, String authorizationHeader);
}
