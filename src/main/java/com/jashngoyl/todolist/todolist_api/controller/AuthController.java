package com.jashngoyl.todolist.todolist_api.controller;

import org.springframework.web.bind.annotation.RestController;

import com.jashngoyl.todolist.todolist_api.dto.AuthenticationRequestDTO;
import com.jashngoyl.todolist.todolist_api.dto.AuthenticationResponseDTO;
import com.jashngoyl.todolist.todolist_api.pojo.AuthenticationRequest;
import com.jashngoyl.todolist.todolist_api.pojo.AuthenticationResponse;
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
@RequestMapping
public class AuthController {

    private ModelMapper modelMapper;

    private UserServiceImpl userServiceImpl;
    public AuthController(ModelMapper modelMapper, UserServiceImpl userServiceImpl) {
        this.modelMapper = modelMapper;
        this.userServiceImpl  = userServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        log.info("Recevied register request: " + authenticationRequest);

        AuthenticationRequestDTO authenticationRequestDTO = modelMapper.map(authenticationRequest, AuthenticationRequestDTO.class);
        log.info("Mapped the request from POJO to DTO"+authenticationRequestDTO);

        AuthenticationResponseDTO authenticationResponseDTO = userServiceImpl.registerUser(authenticationRequestDTO);
        log.info("Recievede the response from serivce layeer"+authenticationResponseDTO);

        AuthenticationResponse authenticationResponse = modelMapper.map(authenticationResponseDTO, AuthenticationResponse.class);
        log.info("Mapped the DTO response to POJO response: "+authenticationResponse);

        ResponseEntity<AuthenticationResponse> responseEntity = new ResponseEntity<>(authenticationResponse, HttpStatus.CREATED);
        log.info("ResponseEntity: "+responseEntity);

        return responseEntity;
    }

}
