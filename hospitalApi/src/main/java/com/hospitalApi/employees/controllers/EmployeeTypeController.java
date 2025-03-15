package com.hospitalApi.employees.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.employees.dtos.EmployeeTypeResponseDTO;
import com.hospitalApi.employees.mappers.EmployeeTypeMapper;
import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.employees.ports.ForEmployeeTypePort;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employee-types")

@RequiredArgsConstructor
public class EmployeeTypeController {

    private final ForEmployeeTypePort employeeTypePort;
    private final EmployeeTypeMapper employeeTypeMapper;

    @Operation(

            summary = "Obtener todos los tipos de empleados", description = "Devuelve la lista de los typos de empleados existentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tipos de empleados obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<EmployeeTypeResponseDTO>> getEmployeesTypes() {

        // mandar a crear el employee al port
        List<EmployeeType> result = employeeTypePort.findAllEmployeesTypes();

        // convertir el Employee al dto
        List<EmployeeTypeResponseDTO> response = employeeTypeMapper
                .fromEmployeeTypeListToEmployeeTypeResponseDtoList(result);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
