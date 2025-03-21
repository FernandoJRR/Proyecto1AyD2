package com.hospitalApi.employees.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.employees.dtos.EmployeeTypeResponseDTO;
import com.hospitalApi.employees.dtos.SaveEmployeeTypeRequestDTO;
import com.hospitalApi.employees.mappers.EmployeeTypeMapper;
import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.employees.ports.ForEmployeeTypePort;
import com.hospitalApi.permissions.mappers.PermissionMapper;
import com.hospitalApi.permissions.models.Permission;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employee-types")

@RequiredArgsConstructor
public class EmployeeTypeController {

        private final ForEmployeeTypePort employeeTypePort;
        private final EmployeeTypeMapper employeeTypeMapper;
        private final PermissionMapper permissionMapper;

        @Operation(summary = "Obtener todos los tipos de empleados", description = "Devuelve la lista de los typos de empleados existentes.")
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

        @Operation(summary = "Crea un nuevo tipo de empleado en el sistema", description = "Este endpoint permite la creación de un nuevo tipo de empleado, asegurando que el nombre no esté duplicado y que los permisos asignados existan en el sistema.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Tipo de empleado creado exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida - Validaciones fallidas"),
                        @ApiResponse(responseCode = "400", description = "Recurso no encontrado - Si algun permiso no se encuentra por medio de los ids especificados"),
                        @ApiResponse(responseCode = "409", description = "Conflicto - Ya existe un tipo de empleado con el mismo nombre"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PostMapping
        public ResponseEntity<EmployeeTypeResponseDTO> createTypeEmployee(
                        @RequestBody @Valid SaveEmployeeTypeRequestDTO request)
                        throws DuplicatedEntryException, NotFoundException {
                // mapeamos la request
                EmployeeType employeeTypeToCreate = employeeTypeMapper.fromCreateEmployeeTypeDtoToEmployeeType(request);
                // creamos los permisos unicamente con sus ids inicializados
                List<Permission> assignedPermissions = permissionMapper
                                .fromIdsRequestDtoToPermissions(request.getPermissions());
                // mandar a crear el employee l port
                EmployeeType savedEmployeeType = employeeTypePort.createEmployeeType(employeeTypeToCreate,
                                assignedPermissions);
                // convertir el EmployeeTyoe al dto
                EmployeeTypeResponseDTO response = employeeTypeMapper
                                .fromEmployeeTypeToEmployeeTypeResponseDto(savedEmployeeType);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PatchMapping("/{employeeTypeId}")
        public ResponseEntity<EmployeeTypeResponseDTO> editEmployeeType(
                        @RequestBody @Valid SaveEmployeeTypeRequestDTO request,
                        @PathVariable("employeeTypeId") String employeeTypeId)
                        throws DuplicatedEntryException, NotFoundException {
                // mapeamos la request
                EmployeeType employeeTypeToUpdate = employeeTypeMapper.fromCreateEmployeeTypeDtoToEmployeeType(request);
                // creamos los permisos unicamente con sus ids inicializados
                List<Permission> assignedPermissions = permissionMapper
                                .fromIdsRequestDtoToPermissions(request.getPermissions());
                // mandar a crear el employee l port
                EmployeeType savedEmployeeType = employeeTypePort.updateEmployeeType(
                                employeeTypeId, employeeTypeToUpdate,
                                assignedPermissions);

                // convertir el EmployeeTyoe al dto
                EmployeeTypeResponseDTO response = employeeTypeMapper
                                .fromEmployeeTypeToEmployeeTypeResponseDto(savedEmployeeType);

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        @DeleteMapping("/{employeeTypeId}")
        public ResponseEntity<EmployeeTypeResponseDTO> editEmployeeType(
                        @PathVariable("employeeTypeId") String employeeTypeId)
                        throws NotFoundException {

                // mandar a crear el employee l port
                boolean isEliminated = employeeTypePort.deleteEmployeeTypeById(
                                employeeTypeId);

                return ResponseEntity.noContent().build();
        }
}
