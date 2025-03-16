package com.hospitalApi.hello.dtos;

import lombok.Value;

@Value
public class HelloResponseDTO {

    private String message;

    public HelloResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}