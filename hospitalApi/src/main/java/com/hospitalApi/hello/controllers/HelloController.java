package com.hospitalApi.hello.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


import com.hospitalApi.hello.dtos.*;


@RestController
@RequestMapping("/api/v1/hello")

@RequiredArgsConstructor
public class HelloController {
    
    @GetMapping("")
    public ResponseEntity<HelloResponseDTO> helloAPI() {
        return ResponseEntity.ok(new HelloResponseDTO("Hello, World!"));
    }
    
}
