package com.hospitalApi.auth.login.dtos;

import java.util.List;

import com.hospitalApi.employees.dtos.EmployeeResponseDTO;
import com.hospitalApi.permissions.dtos.PermissionResponseDTO;

import lombok.Value;

@Value
public class LoginResponseDTO {

    String username;
    EmployeeResponseDTO employee;
    String token;
    List<PermissionResponseDTO> permissions;
}
