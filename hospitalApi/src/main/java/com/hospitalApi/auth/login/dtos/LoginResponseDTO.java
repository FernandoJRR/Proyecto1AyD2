package com.hospitalApi.auth.login.dtos;

import com.hospitalApi.employees.dtos.EmployeeResponseDTO;

import lombok.Value;

@Value
public class LoginResponseDTO {

    String username;
    EmployeeResponseDTO employee;
    String token;
}
