package com.hospitalApi.surgery.controllers;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.dtos.AddDeleteEmployeeSurgeryDTO;
import com.hospitalApi.surgery.dtos.CreateSugeryRequestDTO;
import com.hospitalApi.surgery.dtos.CreateSurgeryTypeRequest;
import com.hospitalApi.surgery.dtos.DeleteSurgeryResponseDTO;
import com.hospitalApi.surgery.dtos.SurgeryEmpleoyeeResponseDTO;
import com.hospitalApi.surgery.dtos.SurgeryResponseDTO;
import com.hospitalApi.surgery.dtos.SurgeryTypeResponseDTO;
import com.hospitalApi.surgery.dtos.UpdateSurgeryTypeRequestDTO;
import com.hospitalApi.surgery.mappers.SurgeryEmployeeMapper;
import com.hospitalApi.surgery.mappers.SurgeryMapper;
import com.hospitalApi.surgery.mappers.SurgeryTypeMapper;
import com.hospitalApi.surgery.models.Surgery;
import com.hospitalApi.surgery.models.SurgeryEmployee;
import com.hospitalApi.surgery.models.SurgeryType;
import com.hospitalApi.surgery.ports.ForSurgeryCreatePort;
import com.hospitalApi.surgery.ports.ForSurgeryEmployeePort;
import com.hospitalApi.surgery.ports.ForSurgeryPort;
import com.hospitalApi.surgery.ports.ForSurgeryTypePort;

import io.swagger.v3.oas.annotations.Operation;
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
	private final ForSurgeryCreatePort surgeryCreatePort;
	private final ForSurgeryEmployeePort surgeryEmployeePort;
	private final ForSurgeryTypePort surgeryTypePort;
	private final SurgeryEmployeeMapper surgeryEmployeeMapper;
	private final SurgeryTypeMapper surgeryTypeMapper;
	private final SurgeryMapper surgeryMapper;

	@Operation(summary = "Obtener todos los tipos de cirugía", description = "Devuelve la lista de los tipos de cirugía existentes.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de tipos de cirugía obtenida exitosamente"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/types/all")
	@ResponseStatus(HttpStatus.OK)
	public List<SurgeryTypeResponseDTO> getAllSurgeryTypes(
			@PathVariable(value = "query", required = false) String query) {
		List<SurgeryType> response = surgeryTypePort.getSurgeryTypes(query);
		List<SurgeryTypeResponseDTO> surgeryTypeResponseDTOList = surgeryTypeMapper
				.fromSurgeryTypeListToSurgeryTypeResponseDTOList(response);
		return surgeryTypeResponseDTOList;
	}

	@Operation(summary = "Crear un nuevo tipo de cirugía", description = "Permite la creación de un nuevo tipo de cirugía en el sistema.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Tipo de cirugía creado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida."),
			@ApiResponse(responseCode = "409", description = "Conflicto - Nombre de tipo de cirugía duplicado")
	})
	@PostMapping("/types/create")
	@ResponseStatus(HttpStatus.CREATED)
	public SurgeryTypeResponseDTO createSurgeryType(
			@Valid @RequestBody CreateSurgeryTypeRequest createSurgeryTypeRequestDTO)
			throws DuplicatedEntryException {
		SurgeryType surgeryType = new SurgeryType(createSurgeryTypeRequestDTO);
		surgeryType = surgeryTypePort.createSurgeryType(surgeryType);
		SurgeryTypeResponseDTO response = surgeryTypeMapper.fromSurgeryTypeToSurgeryTypeResponseDTO(surgeryType);
		return response;
	}

	@Operation(summary = "Actualizar un tipo de cirugía", description = "Permite actualizar un tipo de cirugía existente.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Tipo de cirugía actualizado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida."),
			@ApiResponse(responseCode = "404", description = "Tipo de cirugía no encontrado"),
			@ApiResponse(responseCode = "409", description = "Conflicto - Nombre de tipo de cirugía duplicado")
	})
	@PatchMapping("/types/update/{surgeryTypeId}")
	@ResponseStatus(HttpStatus.OK)
	public SurgeryTypeResponseDTO updateSurgeryType(
			@PathVariable("surgeryTypeId") @NotBlank(message = "El id del tipo de cirugía es requerido") String surgeryTypeId,
			@Valid @RequestBody UpdateSurgeryTypeRequestDTO updateSurgeryTypeRequestDTO)
			throws NotFoundException, DuplicatedEntryException {
		SurgeryType surgeryType = surgeryTypePort.updateSurgeryType(updateSurgeryTypeRequestDTO, surgeryTypeId);
		SurgeryTypeResponseDTO response = surgeryTypeMapper.fromSurgeryTypeToSurgeryTypeResponseDTO(surgeryType);
		return response;
	}

	@Operation(summary = "Obtener un tipo de cirugía por ID", description = "Devuelve un tipo de cirugía por su ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Tipo de cirugía obtenido exitosamente"),
			@ApiResponse(responseCode = "404", description = "Tipo de cirugía no encontrado")
	})
	@GetMapping("/types/{surgeryTypeId}")
	@ResponseStatus(HttpStatus.OK)
	public SurgeryTypeResponseDTO getSurgeryType(
			@PathVariable("surgeryTypeId") @NotBlank(message = "El id del tipo de cirugía es requerido") String surgeryTypeId)
			throws NotFoundException {
		SurgeryType surgeryType = surgeryTypePort.getSurgeryType(surgeryTypeId);
		SurgeryTypeResponseDTO response = surgeryTypeMapper.fromSurgeryTypeToSurgeryTypeResponseDTO(surgeryType);
		return response;
	}

	@Operation(summary = "Obtener todas las cirugías", description = "Devuelve la lista de cirugías existentes.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de cirugías obtenida exitosamente"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/all")
	public List<SurgeryResponseDTO> getAllSurgeries() {
		List<Surgery> surgeryList = surgeryPort.getSurgerys();
		List<SurgeryResponseDTO> response = surgeryMapper.fromSurgeryListToSurgeryResponseDTOList(surgeryList);
		return response;
	}

	@Operation(summary = "Crear una cirugía", description = "Permite crear una nueva cirugía en el sistema.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Cirugía creada exitosamente"),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida."),
			@ApiResponse(responseCode = "404", description = "Consulta o tipo de cirugía no encontrado")
	})
	@PostMapping("/create")
	@ResponseStatus(HttpStatus.CREATED)
	public SurgeryResponseDTO createSurgery(
			@Valid @RequestBody CreateSugeryRequestDTO createSurgeryRequestDTO)
			throws NotFoundException, IllegalStateException, DuplicatedEntryException {
		Surgery surgery = surgeryCreatePort.createSurgery(createSurgeryRequestDTO);
		SurgeryResponseDTO response = surgeryMapper.fromSurgeryToSurgeryResponseDTO(surgery);
		return response;
	}

	@Operation(summary = "Obtener una cirugía por ID", description = "Devuelve una cirugía por su ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Cirugía obtenida exitosamente"),
			@ApiResponse(responseCode = "404", description = "Cirugía no encontrada")
	})
	@GetMapping("{surgeryId}")
	@ResponseStatus(HttpStatus.CREATED)
	public SurgeryResponseDTO getSurgery(
			@PathVariable("surgeryId") @NotBlank(message = "El id de la cirugía es requerido") String surgeryId)
			throws NotFoundException {
		Surgery surgery = surgeryPort.getSurgery(surgeryId);
		SurgeryResponseDTO response = surgeryMapper.fromSurgeryToSurgeryResponseDTO(surgery);
		return response;
	}

	@Operation(summary = "Asignar un doctor a una cirugía", description = "Permite asignar un doctor a una cirugía existente.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Doctor asignado exitosamente a la cirugía"),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida."),
			@ApiResponse(responseCode = "404", description = "Cirugía o empleado no encontrado"),
			@ApiResponse(responseCode = "409", description = "Doctor ya asignado a la cirugía")
	})
	@PostMapping("add-employee")
	@ResponseStatus(HttpStatus.OK)
	public List<SurgeryEmpleoyeeResponseDTO> addEmployeeToSurgery(
			@RequestBody @Valid AddDeleteEmployeeSurgeryDTO addEmployeeSurgeryDTO)
			throws IllegalStateException, NotFoundException, DuplicatedEntryException {
		List<SurgeryEmployee> response = surgeryEmployeePort
				.addDoctorToSurgery(addEmployeeSurgeryDTO.getSurgeryId(),
						addEmployeeSurgeryDTO.getDoctorId(), addEmployeeSurgeryDTO.getIsSpecialist());
		List<SurgeryEmpleoyeeResponseDTO> surgeryEmpleoyeeResponseDTOList = surgeryEmployeeMapper
				.fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(response);
		return surgeryEmpleoyeeResponseDTOList;
	}

	@Operation(summary = "Eliminar un doctor de una cirugía", description = "Permite eliminar un doctor asignado a una cirugía.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Doctor eliminado exitosamente de la cirugía"),
			@ApiResponse(responseCode = "404", description = "Cirugía o doctor no encontrado"),
			@ApiResponse(responseCode = "409", description = "Debe de enviar el tipo de doctor")
	
	})
	@DeleteMapping("remove-employee")
	@ResponseStatus(HttpStatus.OK)
	public List<SurgeryEmpleoyeeResponseDTO> removeEmployeeFromSurgery(
			@RequestBody @Valid AddDeleteEmployeeSurgeryDTO removeEmployeeSurgeryDTO)
			throws NotFoundException, IllegalStateException {
		List<SurgeryEmployee> response = surgeryEmployeePort
				.removeDoctorFromSurgery(removeEmployeeSurgeryDTO.getSurgeryId(),
						removeEmployeeSurgeryDTO.getDoctorId(), removeEmployeeSurgeryDTO.getIsSpecialist());
		List<SurgeryEmpleoyeeResponseDTO> surgeryEmpleoyeeResponseDTOList = surgeryEmployeeMapper
				.fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(response);
		return surgeryEmpleoyeeResponseDTOList;
	}

	@Operation(summary = "Obtener los empleados de una cirugía", description = "Devuelve los empleados asignados a una cirugía.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Empleados de la cirugía obtenidos exitosamente"),
			@ApiResponse(responseCode = "404", description = "Cirugía no encontrada")
	})
	@GetMapping("/surgery-employees/{surgeryId}")
	@ResponseStatus(HttpStatus.OK)
	public List<SurgeryEmpleoyeeResponseDTO> getSurgeryEmployees(
			@PathVariable("surgeryId") @NotBlank(message = "El id de la cirugía es requerido") String surgeryId)
			throws NotFoundException {
		List<SurgeryEmployee> response = surgeryEmployeePort.getSurgeryEmployees(surgeryId);
		List<SurgeryEmpleoyeeResponseDTO> surgeryEmpleoyeeResponseDTOList = surgeryEmployeeMapper
				.fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(response);
		return surgeryEmpleoyeeResponseDTOList;
	}

	@Operation(summary = "Obtiene las cirugías de una consulta", description = "Devuelve la lista de cirugías asociadas a una consulta.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de cirugías obtenida exitosamente"),
			@ApiResponse(responseCode = "404", description = "Consulta no encontrada"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/consult/{consultId}")
	@ResponseStatus(HttpStatus.OK)
	public List<SurgeryResponseDTO> getSugeriesByConsultId(
			@PathVariable("consultId") @NotBlank(message = "El id de la consulta es requerido") String consultId)
			throws NotFoundException {
		List<Surgery> surgeryList = surgeryPort.getSurgerysByConsultId(consultId);
		List<SurgeryResponseDTO> responseDTOs = surgeryMapper
				.fromSurgeryListToSurgeryResponseDTOList(surgeryList);
		return responseDTOs;
	}

	@Operation(summary = "Marcar cirugía como realizada", description = "Permite marcar una cirugía como realizada.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Cirugía marcada como realizada exitosamente"),
			@ApiResponse(responseCode = "404", description = "Cirugía no encontrada"),
			@ApiResponse(responseCode = "409", description = "Conflicto - Cirugía ya marcada como realizada"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@PostMapping("/mark-performed/{surgeryId}")
	@ResponseStatus(HttpStatus.OK)
	public SurgeryResponseDTO markSurgeryAsPerformed(
			@PathVariable("surgeryId") @NotBlank(message = "El id de la cirugía es requerido") String surgeryId)
			throws NotFoundException, IllegalStateException {
		Surgery surgery = surgeryPort.markSurgeryAsPerformed(surgeryId);
		SurgeryResponseDTO response = surgeryMapper.fromSurgeryToSurgeryResponseDTO(surgery);
		return response;
	}

	@Operation(summary = "Eliminar una cirugía", description = "Permite eliminar una cirugía existente.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Cirugía eliminada exitosamente"),
			@ApiResponse(responseCode = "404", description = "Cirugía no encontrada"),
			@ApiResponse(responseCode = "409", description = "Conflicto - Cirugía ya realizada"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@DeleteMapping("{surgeryId}")
	public ResponseEntity<DeleteSurgeryResponseDTO> deleteSurgeryById(
			@PathVariable("surgeryId") @NotBlank(message = "El id de la cirugía es requerido") String surgeryId)
			throws NotFoundException, IllegalStateException {
		DeleteSurgeryResponseDTO response = surgeryPort.deleteSurgery(surgeryId);
		return ResponseEntity.ok().body(response);
	}

}
