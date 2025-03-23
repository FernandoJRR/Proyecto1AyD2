package com.hospitalApi.employees.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.employees.dtos.CreateSpecialistEmpleoyeeRequestDTO;
import com.hospitalApi.employees.dtos.SpecialistEmpleoyeeResponseDTO;
import com.hospitalApi.employees.dtos.UpdateSpecialistEmpleoyeeRequestDTO;
import com.hospitalApi.employees.mappers.SpecialistEmployeeMapper;
import com.hospitalApi.employees.models.SpecialistEmployee;
import com.hospitalApi.employees.ports.ForSpecialistEmployeePort;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/v1/specialist-employees")
@RequiredArgsConstructor
public class SpecialistEmpleoyeeController {

    private final ForSpecialistEmployeePort forSpecialistEmployeePort;
    private final SpecialistEmployeeMapper specialistEmployeeMapper;

    @Operation(summary = "Obtener especialista por ID", description = "Devuelve un especialista en base a su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialista obtenido exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpecialistEmpleoyeeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado, el especialista no existe.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<SpecialistEmpleoyeeResponseDTO> getSpecialistEmployeeById(
            @PathVariable("id") @NotBlank(message = "El id del empleado especialista no puede ser nulo") String id)
            throws NotFoundException {

        SpecialistEmployee specialistEmployee = forSpecialistEmployeePort.getSpecialistEmployeeById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new SpecialistEmpleoyeeResponseDTO(specialistEmployee));
    }

    @Operation(summary = "Obtener especialista por DPI", description = "Devuelve un especialista en base a su DPI.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialista obtenido exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpecialistEmpleoyeeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado, el especialista no existe.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/dpi/{dpi}")
    public ResponseEntity<SpecialistEmpleoyeeResponseDTO> getSpecialistEmployeeByDpi(
            @PathVariable("dpi") @NotBlank(message = "El dpi del empleado especialista no puede ser nulo") String dpi)
            throws NotFoundException {
        SpecialistEmployee specialistEmployee = forSpecialistEmployeePort.getSpecialistEmployeeByDpi(dpi);
        return ResponseEntity.status(HttpStatus.OK).body(new SpecialistEmpleoyeeResponseDTO(specialistEmployee));
    }

    @Operation(summary = "Crear un nuevo especialista", description = "Permite la creación de un nuevo especialista en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Especialista creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpecialistEmpleoyeeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, error en la validación de parámetros.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto - Especialista duplicado.", content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<SpecialistEmpleoyeeResponseDTO> createSpecialistEmployee(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para crear un nuevo especialista", required = true, content = @Content(schema = @Schema(implementation = CreateSpecialistEmpleoyeeRequestDTO.class))) @RequestBody @Valid CreateSpecialistEmpleoyeeRequestDTO createSpecialistEmpleoyeeRequestDTO)
            throws DuplicatedEntryException {
        SpecialistEmployee specialistEmployee = new SpecialistEmployee(createSpecialistEmpleoyeeRequestDTO);
        specialistEmployee = forSpecialistEmployeePort.createSpecialistEmployee(specialistEmployee);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SpecialistEmpleoyeeResponseDTO(specialistEmployee));
    }

    @Operation(summary = "Actualizar un especialista", description = "Permite la edición de un especialista en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialista editado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpecialistEmpleoyeeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, error en la validación de parámetros.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado, el especialista no existe.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto - Especialista duplicado.", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<SpecialistEmpleoyeeResponseDTO> updateSpecialistEmployee(
            @PathVariable("id") @NotBlank(message = "El id del empleado especialista no puede ser nulo") String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para actualizar un especialista", required = true, content = @Content(schema = @Schema(implementation = UpdateSpecialistEmpleoyeeRequestDTO.class))) @RequestBody @Valid UpdateSpecialistEmpleoyeeRequestDTO updateSpecialistEmpleoyeeRequestDTO)
            throws NotFoundException, DuplicatedEntryException {
        SpecialistEmployee specialistEmployee = forSpecialistEmployeePort
                .updateSpecialistEmployee(updateSpecialistEmpleoyeeRequestDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(new SpecialistEmpleoyeeResponseDTO(specialistEmployee));
    }

    @Operation(summary = "Obtener todos los especialistas", description = "Devuelve la lista de todos los especialistas registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de especialistas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpecialistEmpleoyeeResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/all")
    public ResponseEntity<List<SpecialistEmpleoyeeResponseDTO>> getSpecialistEmployees(
            @RequestParam(value = "search", required = false) String search) {

        List<SpecialistEmployee> specialistEmployees = forSpecialistEmployeePort.getSpecialistEmployees(search);
        List<SpecialistEmpleoyeeResponseDTO> response = specialistEmployeeMapper
                .fromSpecialistEmployeeListToSpecialistEmpleoyeeResponseDTOList(specialistEmployees);
        return ResponseEntity.ok().body(response);
    }

}
