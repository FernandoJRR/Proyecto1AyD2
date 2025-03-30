package com.hospitalApi.medicines.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.medicines.dtos.CreateMedicineRequestDTO;
import com.hospitalApi.medicines.dtos.MedicineResponseDTO;
import com.hospitalApi.medicines.dtos.UpdateMedicineRequestDTO;
import com.hospitalApi.medicines.mappers.MedicineMapper;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.ports.ForMedicinePort;
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
@RequestMapping("api/v1/medicines")
@RequiredArgsConstructor
public class MedicineController {

        private final ForMedicinePort medicinePort;
        private final MedicineMapper medicineMapper;

        @Operation(summary = "Obtener todos los medicamentos", description = "Devuelve la lista de los medicamentos existentes.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de medicamentos obtenida exitosamente"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/all")
        @ResponseStatus(HttpStatus.OK)
        public List<MedicineResponseDTO> getAllMedicines(
                        @RequestParam(value = "query", required = false) String query) {
                List<Medicine> medicineList = medicinePort.getAllMedicines(query);
                List<MedicineResponseDTO> response = medicineMapper
                                .fromMedicineListToMedicineResponseDTOList(medicineList);
                return response;
        }

        @Operation(summary = "Obtener los medicamentos con stock bajo", description = "Devuelve la lista de los medicamentos con stock bajo.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de medicamentos con stock bajo obtenida exitosamente"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/low-stock")
        @ResponseStatus(HttpStatus.OK)
        public List<MedicineResponseDTO> getMedicinesWithLowStock() {
                List<Medicine> medicineList = medicinePort.getMedicinesWithLowStock();
                List<MedicineResponseDTO> response = medicineMapper
                                .fromMedicineListToMedicineResponseDTOList(medicineList);
                return response;
        }

        @Operation(summary = "Obtener un medicamento", description = "Devuelve un medicamento en base a su id.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Medicamento obtenido exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Recurso no encontrado, el medicamento no existe."),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        public MedicineResponseDTO getMedicine(
                        @PathVariable("id") @NotBlank(message = "El id del medicamento es requerido") String id)
                        throws NotFoundException {
                // Obtener el medicamento en base al id
                Medicine medicine = medicinePort.getMedicine(id);
                // Convertir el medicamento a un DTO
                MedicineResponseDTO responseDTO = medicineMapper.fromMedicineToMedicineResponseDto(medicine);
                return responseDTO;
        }

        @Operation(summary = "Crear un nuevo medicamento", description = "Este endpoint permite la creación de un nuevo medicamento en el sistema.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Medicamento creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicineResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida, usualmente por error en la validacion de parametros.", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "409", description = "Conflicto - Nombre duplicado", content = @Content(mediaType = "application/json"))
        })
        @PostMapping("/create")
        @PreAuthorize("hasAuthority('CREATE_MEDICINE')")
        @ResponseStatus(HttpStatus.CREATED)
        public MedicineResponseDTO createMedicine(
                        @RequestBody @Valid CreateMedicineRequestDTO createMedicineRequestDTO)
                        throws DuplicatedEntryException {
                // Crear un nuevo medicamento en base al DTO
                Medicine medicine = new Medicine(createMedicineRequestDTO);
                // Creacion del medicamento en el port
                medicine = medicinePort.createMedicine(createMedicineRequestDTO);
                // Convertir el medicamento a un DTO y retornarlo
                MedicineResponseDTO responseDTO = medicineMapper.fromMedicineToMedicineResponseDto(medicine);
                return responseDTO;
        }

        @Operation(summary = "Editar un medicamento", description = "Este endpoint permite la edición de un medicamento en el sistema.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Medicamento editado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicineResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida, usualmente por error en la validacion de parametros.", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "409", description = "Conflicto - Nombre duplicado", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "404", description = "Recursos no econtrados, el medicamento a editar no existe.", content = @Content(mediaType = "application/json")),
        })
        @PatchMapping("/{id}")
        @PreAuthorize("hasAuthority('EDIT_MEDICINE')")
        @ResponseStatus(HttpStatus.OK)
        public MedicineResponseDTO updateMedicine(
                        @PathVariable("id") @NotBlank(message = "El id del medicamento es requerido") String id,
                        @RequestBody @Valid UpdateMedicineRequestDTO updateMedicineRequestDTO)
                        throws DuplicatedEntryException, NotFoundException {
                // Actualizar el medicamento en base al DTO
                Medicine medicine = medicinePort.updateMedicine(id, updateMedicineRequestDTO);
                // Convertir el medicamento a un DTO y retornarlo
                MedicineResponseDTO responseDTO = medicineMapper.fromMedicineToMedicineResponseDto(medicine);
                return responseDTO;
        }
}
