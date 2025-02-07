package com.jashngoyl.todolist.todolist_api.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.jashngoyl.todolist.todolist_api.dto.GetTodosResponseDTO;
import com.jashngoyl.todolist.todolist_api.dto.ToDoRequestDTO;
import com.jashngoyl.todolist.todolist_api.dto.ToDoResponseDTO;

public interface ToDoServiceInterface {

    public ToDoResponseDTO createToDO(ToDoRequestDTO toDoRequestDTO);

    public ToDoResponseDTO updateToDo(Long id, ToDoRequestDTO toDoRequestDTO);

    public void deleteToDo(Long id);

    public GetTodosResponseDTO getToDos(Pageable pageable);
}
