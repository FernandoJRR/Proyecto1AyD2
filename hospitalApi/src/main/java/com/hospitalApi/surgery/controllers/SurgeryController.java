package com.hospitalApi.surgery.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.dtos.*;
import com.hospitalApi.surgery.mappers.SurgeryEmployeeMapper;
import com.hospitalApi.surgery.mappers.SurgeryTypeMapper;
import com.hospitalApi.surgery.models.Surgery;
import com.hospitalApi.surgery.models.SurgeryEmployee;
import com.hospitalApi.surgery.models.SurgeryType;
import com.hospitalApi.surgery.ports.ForSurgeryEmployeePort;
import com.hospitalApi.surgery.ports.ForSurgeryPort;
import com.hospitalApi.surgery.ports.ForSurgeryTypePort;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/surgeries")
@RequiredArgsConstructor
public class SurgeryController {

	private final ForSurgeryPort surgeryPort;
	private final ForSurgeryEmployeePort surgeryEmployeePort;
	private final ForSurgeryTypePort surgeryTypePort;
	private final SurgeryEmployeeMapper surgeryEmployeeMapper;
	private final SurgeryTypeMapper surgeryTypeMapper;

	@Operation(summary = "Obtener todos los tipos de cirugía", description = "Devuelve la lista de los tipos de cirugía existentes.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de tipos de cirugía obtenida exitosamente"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/types/all")
	public ResponseEntity<List<SurgeryTypeResponseDTO>> getAllSurgeryTypes(
			@PathVariable(value = "query", required = false) String query) {
		List<SurgeryType> response = surgeryTypePort.getSurgeryTypes(query);
		List<SurgeryTypeResponseDTO> surgeryTypeResponseDTOList = surgeryTypeMapper
				.fromSurgeryTypeListToSurgeryTypeResponseDTOList(response);
		return ResponseEntity.ok().body(surgeryTypeResponseDTOList);
	}

	@Operation(summary = "Crear un nuevo tipo de cirugía", description = "Permite la creación de un nuevo tipo de cirugía en el sistema.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Tipo de cirugía creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurgeryTypeResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida.", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "409", description = "Conflicto - Nombre de tipo de cirugía duplicado", content = @Content(mediaType = "application/json"))
	})
	@PostMapping("/types/create")
	public ResponseEntity<SurgeryTypeResponseDTO> createSurgeryType(
			@Valid @RequestBody CreateSurgeryTypeRequest createSurgeryTypeRequestDTO)
			throws DuplicatedEntryException {
		SurgeryType surgeryType = new SurgeryType(createSurgeryTypeRequestDTO);
		surgeryType = surgeryTypePort.createSurgeryType(surgeryType);
		SurgeryTypeResponseDTO response = new SurgeryTypeResponseDTO(surgeryType);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Actualizar un tipo de cirugía", description = "Permite actualizar un tipo de cirugía existente.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Tipo de cirugía actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurgeryTypeResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida.", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "404", description = "Tipo de cirugía no encontrado", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "409", description = "Conflicto - Nombre de tipo de cirugía duplicado", content = @Content(mediaType = "application/json"))
	})
	@PatchMapping("/types/update/{surgeryTypeId}")
	public ResponseEntity<SurgeryTypeResponseDTO> updateSurgeryType(
			@PathVariable("surgeryTypeId") @NotBlank(message = "El id del tipo de cirugía es requerido") String surgeryTypeId,
			@Valid @RequestBody UpdateSurgeryTypeRequestDTO updateSurgeryTypeRequestDTO)
			throws NotFoundException, DuplicatedEntryException {
		SurgeryType surgeryType = surgeryTypePort.updateSurgeryType(updateSurgeryTypeRequestDTO, surgeryTypeId);
		SurgeryTypeResponseDTO response = new SurgeryTypeResponseDTO(surgeryType);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Obtener un tipo de cirugía por ID", description = "Devuelve un tipo de cirugía por su ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Tipo de cirugía obtenido exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurgeryTypeResponseDTO.class))),
			@ApiResponse(responseCode = "404", description = "Tipo de cirugía no encontrado", content = @Content(mediaType = "application/json"))
	})
	@GetMapping("/types/{surgeryTypeId}")
	public ResponseEntity<SurgeryTypeResponseDTO> getSurgeryType(
			@PathVariable("surgeryTypeId") @NotBlank(message = "El id del tipo de cirugía es requerido") String surgeryTypeId)
			throws NotFoundException {
		SurgeryType surgeryType = surgeryTypePort.getSurgeryType(surgeryTypeId);
		SurgeryTypeResponseDTO response = new SurgeryTypeResponseDTO(surgeryType);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Obtener todas las cirugías", description = "Devuelve la lista de cirugías existentes.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de cirugías obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurgeryResponseDTO.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/all")
	public ResponseEntity<List<SurgeryResponseDTO>> getAllSurgeries() {
		List<Surgery> surgeryList = surgeryPort.getSurgerys();
		List<SurgeryResponseDTO> response = surgeryList.stream().map(surgery -> {
			SurgeryTypeResponseDTO surgeryType = new SurgeryTypeResponseDTO(surgery.getSurgeryType());
			List<SurgeryEmpleoyeeResponseDTO> surgeryEmployees = surgeryEmployeeMapper
					.fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(
							surgery.getSurgeryEmployees());
			return new SurgeryResponseDTO(surgery, surgeryType, surgeryEmployees);
		}).toList();
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Crear una cirugía", description = "Permite crear una nueva cirugía en el sistema.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Cirugía creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurgeryResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida.", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "404", description = "Consulta o tipo de cirugía no encontrado", content = @Content(mediaType = "application/json"))
	})
	@PostMapping("/create")
	public ResponseEntity<SurgeryResponseDTO> createSurgery(
			@Valid @RequestBody CreateSugeryRequestDTO createSurgeryRequestDTO) throws NotFoundException {
		Surgery surgery = surgeryPort.createSurgery(createSurgeryRequestDTO.getConsultId(),
				createSurgeryRequestDTO.getSurgeryTypeId());
		SurgeryTypeResponseDTO surgeryType = new SurgeryTypeResponseDTO(surgery.getSurgeryType());
		List<SurgeryEmpleoyeeResponseDTO> surgeryEmployees = surgeryEmployeeMapper
				.fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(
						surgery.getSurgeryEmployees());
		SurgeryResponseDTO response = new SurgeryResponseDTO(surgery, surgeryType, surgeryEmployees);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Obtener una cirugía por ID", description = "Devuelve una cirugía por su ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Cirugía obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurgeryResponseDTO.class))),
			@ApiResponse(responseCode = "404", description = "Cirugía no encontrada", content = @Content(mediaType = "application/json"))
	})
	@GetMapping("{surgeryId}")
	public ResponseEntity<SurgeryResponseDTO> getSurgery(
			@PathVariable("surgeryId") @NotBlank(message = "El id de la cirugía es requerido") String surgeryId)
			throws NotFoundException {
		Surgery surgery = surgeryPort.getSurgery(surgeryId);
		SurgeryTypeResponseDTO surgeryType = new SurgeryTypeResponseDTO(surgery.getSurgeryType());
		List<SurgeryEmpleoyeeResponseDTO> surgeryEmployees = surgeryEmployeeMapper
				.fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(
						surgery.getSurgeryEmployees());
		SurgeryResponseDTO response = new SurgeryResponseDTO(surgery, surgeryType, surgeryEmployees);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Asignar un empleado a una cirugía", description = "Permite asignar un empleado a una cirugía existente.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Empleado asignado exitosamente a la cirugía", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurgeryEmpleoyeeResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida.", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "404", description = "Cirugía o empleado no encontrado", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "409", description = "Empleado ya asignado a la cirugía", content = @Content(mediaType = "application/json"))
	})
	@PostMapping("add-employee")
	public ResponseEntity<List<SurgeryEmpleoyeeResponseDTO>> addEmployeeToSurgery(
			@RequestBody @Valid AddDeleteEmployeeSurgeryDTO addEmployeeSurgeryDTO)
			throws NotFoundException, DuplicatedEntryException {
		List<SurgeryEmployee> response = surgeryEmployeePort
				.addEmpleoyeeToSurgery(addEmployeeSurgeryDTO.getSurgeryId(),
						addEmployeeSurgeryDTO.getEmployeeId());
		List<SurgeryEmpleoyeeResponseDTO> surgeryEmpleoyeeResponseDTOList = surgeryEmployeeMapper
				.fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(response);
		return ResponseEntity.ok().body(surgeryEmpleoyeeResponseDTOList);
	}

	@Operation(summary = "Eliminar un empleado de una cirugía", description = "Permite eliminar un empleado asignado a una cirugía.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Empleado eliminado exitosamente de la cirugía", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurgeryEmpleoyeeResponseDTO.class))),
			@ApiResponse(responseCode = "404", description = "Cirugía o empleado no encontrado", content = @Content(mediaType = "application/json"))
	})
	@DeleteMapping("remove-employee")
	public ResponseEntity<List<SurgeryEmpleoyeeResponseDTO>> removeEmployeeFromSurgery(
			@RequestBody @Valid AddDeleteEmployeeSurgeryDTO removeEmployeeSurgeryDTO)
			throws NotFoundException {
		List<SurgeryEmployee> response = surgeryEmployeePort
				.removeEmployeeFromSurgery(removeEmployeeSurgeryDTO.getSurgeryId(),
						removeEmployeeSurgeryDTO.getEmployeeId());
		List<SurgeryEmpleoyeeResponseDTO> surgeryEmpleoyeeResponseDTOList = surgeryEmployeeMapper
				.fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(response);
		return ResponseEntity.ok().body(surgeryEmpleoyeeResponseDTOList);
	}

	@Operation(summary = "Asignar un especialista a una cirugía", description = "Permite asignar un especialista a una cirugía existente.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Especialista asignado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurgeryEmpleoyeeResponseDTO.class))),
			@ApiResponse(responseCode = "404", description = "Cirugía o especialista no encontrado", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "409", description = "Especialista ya asignado a la cirugía", content = @Content(mediaType = "application/json"))
	})
	@PostMapping("add-specialist")
	public ResponseEntity<List<SurgeryEmpleoyeeResponseDTO>> addSpecialistToSurgery(
			@RequestBody @Valid AddDeleteEmployeeSurgeryDTO addSpecialistSurgeryDTO)
			throws NotFoundException, DuplicatedEntryException {
		List<SurgeryEmployee> response = surgeryEmployeePort
				.addSpecialistToSurgery(addSpecialistSurgeryDTO.getSurgeryId(),
						addSpecialistSurgeryDTO.getEmployeeId());
		List<SurgeryEmpleoyeeResponseDTO> surgeryEmpleoyeeResponseDTOList = surgeryEmployeeMapper
				.fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(response);
		return ResponseEntity.ok().body(surgeryEmpleoyeeResponseDTOList);
	}

	@Operation(summary = "Eliminar un especialista de una cirugía", description = "Permite eliminar un especialista asignado a una cirugía.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Especialista eliminado exitosamente de la cirugía", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurgeryEmpleoyeeResponseDTO.class))),
			@ApiResponse(responseCode = "404", description = "Cirugía o especialista no encontrado", content = @Content(mediaType = "application/json"))
	})
	@DeleteMapping("remove-specialist")
	public ResponseEntity<List<SurgeryEmpleoyeeResponseDTO>> removeSpecialistFromSurgery(
			@RequestBody @Valid AddDeleteEmployeeSurgeryDTO removeSpecialistSurgeryDTO)
			throws NotFoundException {
		List<SurgeryEmployee> response = surgeryEmployeePort
				.removeSpecialistFromSurgery(removeSpecialistSurgeryDTO.getSurgeryId(),
						removeSpecialistSurgeryDTO.getEmployeeId());
		List<SurgeryEmpleoyeeResponseDTO> surgeryEmpleoyeeResponseDTOList = surgeryEmployeeMapper
				.fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(response);
		return ResponseEntity.ok().body(surgeryEmpleoyeeResponseDTOList);
	}

	@Operation(summary = "Obtener los empleados de una cirugía", description = "Devuelve los empleados asignados a una cirugía.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Empleados de la cirugía obtenidos exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurgeryEmpleoyeeResponseDTO.class))),
			@ApiResponse(responseCode = "404", description = "Cirugía no encontrada", content = @Content(mediaType = "application/json"))
	})
	@GetMapping("/surgery-employees/{surgeryId}")
	public ResponseEntity<List<SurgeryEmpleoyeeResponseDTO>> getSurgeryEmployees(
			@PathVariable("surgeryId") @NotBlank(message = "El id de la cirugía es requerido") String surgeryId)
			throws NotFoundException {
		List<SurgeryEmployee> response = surgeryEmployeePort.getSurgeryEmployees(surgeryId);
		List<SurgeryEmpleoyeeResponseDTO> surgeryEmpleoyeeResponseDTOList = surgeryEmployeeMapper
				.fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(response);
		return ResponseEntity.ok().body(surgeryEmpleoyeeResponseDTOList);
	}

	@Operation(summary = "Obtiene las cirugías de una consulta", description = "Devuelve la lista de cirugías asociadas a una consulta.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de cirugías obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurgeryResponseDTO.class))),
			@ApiResponse(responseCode = "404", description = "Consulta no encontrada", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/consult/{consultId}")
	public ResponseEntity<List<SurgeryResponseDTO>> getSugeriesByConsultId(
			@PathVariable("consultId") @NotBlank(message = "El id de la consulta es requerido") String consultId)
			throws NotFoundException {
		List<Surgery> surgeryList = surgeryPort.getSurgerysByConsultId(consultId);
		List<SurgeryResponseDTO> response = surgeryList.stream().map(surgery -> {
			SurgeryTypeResponseDTO surgeryType = new SurgeryTypeResponseDTO(surgery.getSurgeryType());
			List<SurgeryEmpleoyeeResponseDTO> surgeryEmployees = surgeryEmployeeMapper
					.fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(
							surgery.getSurgeryEmployees());
			return new SurgeryResponseDTO(surgery, surgeryType, surgeryEmployees);
		}).toList();
		return ResponseEntity.ok().body(response);
	}

}
