package com.hospitalApi.employees.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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

@RestController
@RequestMapping("/api/v1/specialist-employees")
@RequiredArgsConstructor
public class SpecialistEmpleoyeeController {

	private final ForSpecialistEmployeePort forSpecialistEmployeePort;
	private final SpecialistEmployeeMapper specialistEmployeeMapper;

	@Operation(summary = "Obtener especialista por ID", description = "Devuelve un especialista en base a su ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Especialista obtenido exitosamente"),
			@ApiResponse(responseCode = "404", description = "Recurso no encontrado, el especialista no existe."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/id/{id}")
	@ResponseStatus(HttpStatus.OK)
	public SpecialistEmpleoyeeResponseDTO getSpecialistEmployeeById(
			@PathVariable("id") @NotBlank(message = "El id del empleado especialista no puede ser nulo") String id)
			throws NotFoundException {

		SpecialistEmployee specialistEmployee = forSpecialistEmployeePort.getSpecialistEmployeeById(id);
		return specialistEmployeeMapper.fromSpecialistEmployeeToSpecialistEmpleoyeeResponseDTO(specialistEmployee);
	}

	@Operation(summary = "Obtener especialista por DPI", description = "Devuelve un especialista en base a su DPI.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Especialista obtenido exitosamente"),
			@ApiResponse(responseCode = "404", description = "Recurso no encontrado, el especialista no existe."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/dpi/{dpi}")
	@ResponseStatus(HttpStatus.OK)
	public SpecialistEmpleoyeeResponseDTO getSpecialistEmployeeByDpi(
			@PathVariable("dpi") @NotBlank(message = "El dpi del empleado especialista no puede ser nulo") String dpi)
			throws NotFoundException {
		SpecialistEmployee specialistEmployee = forSpecialistEmployeePort.getSpecialistEmployeeByDpi(dpi);
		return specialistEmployeeMapper.fromSpecialistEmployeeToSpecialistEmpleoyeeResponseDTO(specialistEmployee);
	}

	@Operation(summary = "Crear un nuevo especialista", description = "Permite la creación de un nuevo especialista en el sistema.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Especialista creado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida, error en la validación de parámetros."),
			@ApiResponse(responseCode = "409", description = "Conflicto - Especialista duplicado.")
	})
	@PostMapping("/create")
	@ResponseStatus(HttpStatus.CREATED)
	public SpecialistEmpleoyeeResponseDTO createSpecialistEmployee(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para crear un nuevo especialista", required = true, content = @Content(schema = @Schema(implementation = CreateSpecialistEmpleoyeeRequestDTO.class))) @RequestBody @Valid CreateSpecialistEmpleoyeeRequestDTO createSpecialistEmpleoyeeRequestDTO)
			throws DuplicatedEntryException {
		SpecialistEmployee specialistEmployee = new SpecialistEmployee(createSpecialistEmpleoyeeRequestDTO);
		specialistEmployee = forSpecialistEmployeePort.createSpecialistEmployee(specialistEmployee);
		return specialistEmployeeMapper
				.fromSpecialistEmployeeToSpecialistEmpleoyeeResponseDTO(specialistEmployee);
	}

	@Operation(summary = "Actualizar un especialista", description = "Permite la edición de un especialista en el sistema.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Especialista editado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida, error en la validación de parámetros."),
			@ApiResponse(responseCode = "404", description = "Recurso no encontrado, el especialista no existe."),
			@ApiResponse(responseCode = "409", description = "Conflicto - Especialista duplicado.")
	})
	@PatchMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public SpecialistEmpleoyeeResponseDTO updateSpecialistEmployee(
			@PathVariable("id") @NotBlank(message = "El id del empleado especialista no puede ser nulo") String id,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para actualizar un especialista", required = true, content = @Content(schema = @Schema(implementation = UpdateSpecialistEmpleoyeeRequestDTO.class))) @RequestBody @Valid UpdateSpecialistEmpleoyeeRequestDTO updateSpecialistEmpleoyeeRequestDTO)
			throws NotFoundException, DuplicatedEntryException {
		SpecialistEmployee specialistEmployee = forSpecialistEmployeePort
				.updateSpecialistEmployee(updateSpecialistEmpleoyeeRequestDTO, id);
		return specialistEmployeeMapper.fromSpecialistEmployeeToSpecialistEmpleoyeeResponseDTO(specialistEmployee);
	}

	@Operation(summary = "Obtener todos los especialistas", description = "Devuelve la lista de todos los especialistas registrados.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de especialistas obtenida exitosamente"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/all")
	@ResponseStatus(HttpStatus.OK)
	public List<SpecialistEmpleoyeeResponseDTO> getSpecialistEmployees(
			@RequestParam(value = "search", required = false) String search) {

		List<SpecialistEmployee> specialistEmployees = forSpecialistEmployeePort.getSpecialistEmployees(search);
		List<SpecialistEmpleoyeeResponseDTO> response = specialistEmployeeMapper
				.fromSpecialistEmployeeListToSpecialistEmpleoyeeResponseDTOList(specialistEmployees);
		return response;
	}

}
