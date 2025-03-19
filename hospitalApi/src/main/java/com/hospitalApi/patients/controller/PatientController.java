package com.hospitalApi.patients.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.patients.dtos.CreatePatientRequestDTO;
import com.hospitalApi.patients.dtos.PatientResponseDTO;
import com.hospitalApi.patients.mappers.PatientMapper;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.patients.services.PatientService;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/v1/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @Operation(summary = "Obtener todos los pacientes", description = "Devuelve la lista de los pacientes existentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pacientes obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/all")
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients(
            @RequestParam(value = "query", required = false) String query) {
        List<Patient> patients;
        if (query != null) {
            patients = patientService.searchPatients(query);
        } else {
            patients = patientService.getPatients();
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(patientMapper.fromPatientListToPatientResponseDTOList(patients));
    }

    @Operation(summary = "Obtener un paciente por ID", description = "Devuelve un paciente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente obtenido exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, usualmente por error en la validacion de parametros.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<PatientResponseDTO> getPatientById(
            @PathVariable("id") @NotBlank(message = "El ID del paciente es requerido") String id)
            throws NotFoundException {
        Patient patient = patientService.getPatient(id);
        return ResponseEntity.status(HttpStatus.OK).body(patientMapper.toPatientResponseDTO(patient));
    }

    @Operation(summary = "Obtener un paciente por DPI", description = "Devuelve un paciente por su DPI.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente obtenido exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, usualmente por error en la validacion de parametros.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/dpi/{dpi}")
    public ResponseEntity<PatientResponseDTO> getPatientByDpi(
            @PathVariable("dpi") @NotBlank(message = "El DPI del paciente es requerido") String dpi
    ) throws NotFoundException {
        Patient patient = patientService.getPatientByDpi(dpi);
        return ResponseEntity.status(HttpStatus.OK).body(patientMapper.toPatientResponseDTO(patient));
    }


    @Operation(summary = "Crear un nuevo paciente", description = "Este endpoint permite la creación de un nuevo paciente en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Paciente creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, usualmente por error en la validacion de parametros.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflicto - DPI duplicado", content = @Content(mediaType = "application/json"))
    })    
    @GetMapping("/create")
    public ResponseEntity<PatientResponseDTO> createPatient(
            @RequestBody @Valid CreatePatientRequestDTO createPatientRequestDTO) throws DuplicatedEntryException {
        Patient patient = new Patient(createPatientRequestDTO);
        patient = patientService.createPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(patientMapper.toPatientResponseDTO(patient));
    }

}
