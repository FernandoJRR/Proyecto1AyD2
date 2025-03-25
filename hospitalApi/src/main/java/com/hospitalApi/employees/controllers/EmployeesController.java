package com.hospitalApi.employees.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.employees.dtos.CreateEmployeeRequestDTO;
import com.hospitalApi.employees.dtos.EmployeeRequestDTO;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

        @Operation(summary = "Crear un nuevo empleado", description = "Este endpoint permite la creación de un nuevo empleado en el sistema.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Empleado creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida, usualmente por error en la validacion de parametros.", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "409", description = "Conflicto - Username duplicado", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "404", description = "No encontrado - Tipo de empleado no existe", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PostMapping
        public ResponseEntity<EmployeeResponseDTO> createEmployee(
                        @RequestBody @Valid CreateEmployeeRequestDTO request)
                        throws DuplicatedEntryException, NotFoundException {
                // extraer los parametros para la creacion del employee
                Employee newEmployee = employeeMapper.fromCreateEmployeeRequestDtoToEmployee(request);
                EmployeeType employeeType = employeeTypeMapper
                                .fromIdRequestDtoToEmployeeType(request.getEmployeeTypeId());
                User newUser = userMapper.fromCreateUserRequestDtoToUser(request.getCreateUserRequestDTO());

                // mandar a crear el employee al port
                Employee result = employeesPort.createEmployee(newEmployee, employeeType, newUser);

                // convertir el Employee al dto
                EmployeeResponseDTO response = employeeMapper.fromEmployeeToResponse(result);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @Operation(summary = "Edita un empleado", description = "Este endpoint permite la edición de un empleado en el sistema.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Empleado editado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida, usualmente por error en la validacion de parametros.", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "404", description = "Recursos no econtrados, el usuario a editar no existe o el tipo de empleado no existe.", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")

        })
        @PreAuthorize("hasAuthority('EDITAR_EMPLEADOS')")//el usuario necesita este permiso para entrar al endpoint
        @PatchMapping("/{employeeId}")
        public ResponseEntity<EmployeeResponseDTO> updateEmployee(
                        @PathVariable("employeeId") String employeeId,
                        @RequestBody @Valid EmployeeRequestDTO request)
                        throws NotFoundException {

                // extraer los parametros para la creacion del employee
                Employee newEmployee = employeeMapper.fromEmployeeRequestDtoToEmployee(request);
                EmployeeType employeeType = employeeTypeMapper
                                .fromIdRequestDtoToEmployeeType(request.getEmployeeTypeId());

                // mandar a editar el employee al port
                Employee result = employeesPort.updateEmployee(employeeId, newEmployee, employeeType);

                // convertir el Employee al dto
                EmployeeResponseDTO response = employeeMapper.fromEmployeeToResponse(result);

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        @Operation(summary = "Edita un empleado", description = "Este endpoint permite la cambiar el estado de desactivatedAt de un empleado en el sistema segun su id.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Empleado desactivado exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida, usualmente por error en la validacion de parametros.", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "404", description = "Recursos no econtrados, el usuario a desactivar.", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "409", description = "Conflicto, el empleaod ya esta desactivado.", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PatchMapping("/{employeeId}/desactivate")
        public ResponseEntity<Void> desactivateEmployee(
                        @PathVariable("employeeId") String employeeId)
                        throws NotFoundException, IllegalStateException {

                // mandar a desactivar el employee al port
                employeesPort.desactivateEmployee(employeeId);

                return ResponseEntity.noContent().build();

        }

        @Operation(summary = "Busca un empleado", description = "Este endpoint permite la busqueda de un empleado en base a su Id.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Empleado encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida, usualmente por error en la validacion de parametros.", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado.", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/{employeeId}")
        public ResponseEntity<EmployeeResponseDTO> findEmployeeById(
                        @PathVariable("employeeId") String employeeId)
                        throws NotFoundException {

                // mandar a crear el employee al port
                Employee result = employeesPort.findEmployeeById(employeeId);

                // convertir el Employee al dto
                EmployeeResponseDTO response = employeeMapper.fromEmployeeToResponse(result);

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        @Operation(summary = "Obtener todos los empleados", description = "Este endpoint permite la busqueda de todos los empleados.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Empleados encontrados exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida, usualmente por error en la validacion de parametros.", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/")
        public ResponseEntity<List<EmployeeResponseDTO>> findEmployees() {
                // mandar a crear el employee al port
                List<Employee> result = employeesPort.findEmployees();

                // convertir el Employee al dto
                List<EmployeeResponseDTO> response = employeeMapper.fromEmployeesToResponse(result);

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }
}
