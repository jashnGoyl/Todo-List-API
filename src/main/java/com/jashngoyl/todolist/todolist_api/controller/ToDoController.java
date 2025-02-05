package com.jashngoyl.todolist.todolist_api.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jashngoyl.todolist.todolist_api.dto.ToDoRequestDTO;
import com.jashngoyl.todolist.todolist_api.dto.ToDoResponseDTO;
import com.jashngoyl.todolist.todolist_api.pojo.ToDoRequest;
import com.jashngoyl.todolist.todolist_api.pojo.ToDoResponse;
import com.jashngoyl.todolist.todolist_api.service.impl.ToDoServiceImpl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/todo-list")
@Slf4j
public class ToDoController {

    private ModelMapper modelMapper;

    private ToDoServiceImpl toDoServiceImpl;

    public ToDoController(ModelMapper modelMapper, ToDoServiceImpl toDoServiceImpl){
        this.modelMapper = modelMapper;
        this.toDoServiceImpl = toDoServiceImpl;
    }

    @PostMapping("/create")
    public ResponseEntity<ToDoResponse> createToDo(@Valid @RequestBody ToDoRequest toDoRequest){

        log.info("Received a TODO request: "+toDoRequest);
        
        ToDoRequestDTO toDoRequestDTO = modelMapper.map(toDoRequest, ToDoRequestDTO.class);
        log.info("Mapped Todo Request from pojo to DTO: "+toDoRequestDTO);

        ToDoResponseDTO toDoResponseDTO = toDoServiceImpl.createToDO(toDoRequestDTO);
        log.info("ToDoResponseDTO: "+toDoResponseDTO);

        ToDoResponse toDoResponse = modelMapper.map(toDoResponseDTO, ToDoResponse.class);
        log.info("Mapped the todo response from DTO to pojo: "+toDoResponse);

        ResponseEntity<ToDoResponse> responseEntity = new ResponseEntity<ToDoResponse>(toDoResponse, HttpStatus.CREATED);
        log.info("ResponseEntity: "+responseEntity);

        return responseEntity;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToDoResponse> updateToDo(@PathVariable Long id,@Valid @RequestBody ToDoRequest toDoRequest) {
        log.info("Recieved id to update ToDo: "+id);

        log.info("Recieved the todo request: "+toDoRequest);

        ToDoRequestDTO toDoRequestDTO = modelMapper.map(toDoRequest, ToDoRequestDTO.class);
        log.info("Mapped todo request POJO to DTO: "+toDoRequestDTO);

        ToDoResponseDTO toDoResponseDTO = toDoServiceImpl.updateToDo(id, toDoRequestDTO);
        log.info("Recieved todo response in controller: "+toDoResponseDTO);

        ToDoResponse toDoResponse = modelMapper.map(toDoResponseDTO, ToDoResponse.class);
        log.info("Mapped todo response DTO to POJO: "+toDoResponse);

        ResponseEntity<ToDoResponse> responseEntity = new ResponseEntity<ToDoResponse>(toDoResponse, HttpStatus.OK);
        log.info("ResponseEntity: "+responseEntity);
        
        return responseEntity;
    }
    

}
