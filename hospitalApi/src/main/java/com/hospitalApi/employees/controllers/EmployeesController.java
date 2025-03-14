package com.hospitalApi.employees.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.employees.dtos.CreateEmployeeRequestDTO;
import com.hospitalApi.employees.dtos.EmployeeResponseDTO;
import com.hospitalApi.employees.mappers.EmployeeMapper;
import com.hospitalApi.employees.mappers.EmployeeTypeMapper;
import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.employees.ports.ForEmployeesPort;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.users.mappers.UserMapper;
import com.hospitalApi.users.models.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employees")

@RequiredArgsConstructor
public class EmployeesController {

    private final ForEmployeesPort employeesPort;

    private final EmployeeTypeMapper employeeTypeMapper;
    private final EmployeeMapper employeeMapper;
    private final UserMapper userMapper;

    @PostMapping("/create-employee")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(
            @RequestBody @Valid CreateEmployeeRequestDTO request) throws DuplicatedEntryException, NotFoundException {
        // extraer los parametros para la creacion del employee
        Employee newEmployee = employeeMapper.fromCreateEmployeeRequestDtoToEmployee(request);
        EmployeeType employeeType = employeeTypeMapper.fromIdRequestDtoTo(request.getEmployeeTypeId());
        User newUser = userMapper.fromCreateUserRequestDtoToUser(request.getCreateUserRequestDTO());

        // mandar a crear el employee al port
        Employee result = employeesPort.createEmployee(newEmployee, employeeType, newUser);

        // convertir el Employee al dto
        EmployeeResponseDTO response = employeeMapper.fromEmployeeToResponse(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
