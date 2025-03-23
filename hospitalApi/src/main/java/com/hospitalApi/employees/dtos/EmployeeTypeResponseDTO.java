package com.hospitalApi.employees.dtos;

import java.util.List;

import com.hospitalApi.permissions.dtos.PermissionResponseDTO;

import lombok.Value;

@Value
public class EmployeeTypeResponseDTO {

    String id;
    String name;
    List<PermissionResponseDTO> permissions;
}
