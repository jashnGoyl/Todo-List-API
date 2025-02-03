package com.jashngoyl.todolist.todolist_api.controller;

import org.springframework.web.bind.annotation.RestController;

import com.jashngoyl.todolist.todolist_api.dto.AuthenticationResponseDTO;
import com.jashngoyl.todolist.todolist_api.dto.LoginRequestDTO;
import com.jashngoyl.todolist.todolist_api.dto.RegisterRequestDTO;
import com.jashngoyl.todolist.todolist_api.pojo.RegisterRequest;
import com.jashngoyl.todolist.todolist_api.pojo.AuthenticationResponse;
import com.jashngoyl.todolist.todolist_api.pojo.LoginRequest;
import com.jashngoyl.todolist.todolist_api.service.impl.UserServiceImpl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@Slf4j
@RequestMapping("/todo-list/auth")
public class AuthController {

    private ModelMapper modelMapper;

    private UserServiceImpl userServiceImpl;
    public AuthController(ModelMapper modelMapper, UserServiceImpl userServiceImpl) {
        this.modelMapper = modelMapper;
        this.userServiceImpl  = userServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Recevied register request: " + registerRequest);

        RegisterRequestDTO registerRequestDTO = modelMapper.map(registerRequest, RegisterRequestDTO.class);
        log.info("Mapped the request from POJO to DTO"+registerRequestDTO);

        AuthenticationResponseDTO authenticationResponseDTO = userServiceImpl.registerUser(registerRequestDTO);
        log.info("Recievede the response from serivce layeer"+authenticationResponseDTO);

        AuthenticationResponse authenticationResponse = modelMapper.map(authenticationResponseDTO, AuthenticationResponse.class);
        log.info("Mapped the DTO response to POJO response: "+authenticationResponse);

        ResponseEntity<AuthenticationResponse> responseEntity = new ResponseEntity<>(authenticationResponse, HttpStatus.CREATED);
        log.info("ResponseEntity: "+responseEntity);

        return responseEntity;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest){
        log.info("Recieved Login Request: "+loginRequest);

        LoginRequestDTO loginRequestDTO = modelMapper.map(loginRequest, LoginRequestDTO.class);
        log.info("Mapped LoginRequest to DTO: "+loginRequestDTO);

        AuthenticationResponseDTO authenticationResponseDTO = userServiceImpl.loginUser(loginRequestDTO);
        log.info("Recevied response from service layer: "+authenticationResponseDTO);
        
        AuthenticationResponse authenticationResponse = modelMapper.map(authenticationResponseDTO, AuthenticationResponse.class);
        log.info("Mapped the response from DTO to pojo: "+authenticationResponse);

        ResponseEntity<AuthenticationResponse> responseEntity = new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
        log.info("ResponseEntity: "+responseEntity);

        return responseEntity;
    }

}
