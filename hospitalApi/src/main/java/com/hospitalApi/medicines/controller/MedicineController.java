package com.hospitalApi.medicines.controller;

import com.hospitalApi.medicines.dtos.CreateMedicineRequestDTO;
import com.hospitalApi.medicines.mappers.MedicineMapper;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.ports.ForMedicinePort;
import com.hospitalApi.medicines.services.MedicineService;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.hospitalApi.medicines.dtos.MedicineResponseDTO;
import com.hospitalApi.medicines.dtos.UpdateMedicineRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("v1/medicines")
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
    public ResponseEntity<List<MedicineResponseDTO>> getAllMedicines() {
        List<Medicine> medicineList = medicinePort.getAllMedicines();
        List<MedicineResponseDTO> response = medicineMapper.fromMedicineListToMedicineResponseDTOList(medicineList);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponseDTO> getMedicine(
            @PathVariable("id") @NotBlank(message = "El id del medicamento es requerido") Long id
    ) throws NotFoundException {
        //Obtener el medicamento en base al id
        Medicine medicine = medicinePort.getMedicine(id);
        //Convertir el medicamento a un DTO
        MedicineResponseDTO responseDTO = new MedicineResponseDTO(medicine);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @Operation(summary = "Crear un nuevo medicamento", description = "Este endpoint permite la creación de un nuevo medicamento en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Medicamento creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, usualmente por error en la validacion de parametros.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflicto - Nombre duplicado", content = @Content(mediaType = "application/json"))
    })
    @PostMapping()
    public ResponseEntity<MedicineResponseDTO> createMedicine(
            @RequestBody @Valid CreateMedicineRequestDTO createMedicineRequestDTO
    ) throws DuplicatedEntryException {
        // Crear un nuevo medicamento en base al DTO
        Medicine medicine = new Medicine(createMedicineRequestDTO);
        // Creacion del medicamento en el port
        medicine = medicinePort.createMedicine(createMedicineRequestDTO);
        // Convertir el medicamento a un DTO y retornarlo
        MedicineResponseDTO responseDTO = new MedicineResponseDTO(medicine);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Editar un medicamento", description = "Este endpoint permite la edición de un medicamento en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicamento editado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, usualmente por error en la validacion de parametros.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflicto - Nombre duplicado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Recursos no econtrados, el medicamento a editar no existe.", content = @Content(mediaType = "application/json")),
    })
    @PatchMapping("/{id}")
    public ResponseEntity<MedicineResponseDTO> updateMedicine(
            @PathVariable("id") @NotBlank(message = "El id del medicamento es requerido") Long id,
            @RequestBody @Valid UpdateMedicineRequestDTO updateMedicineRequestDTO) throws DuplicatedEntryException, NotFoundException {
        // Actualizar el medicamento en base al DTO
        Medicine medicine = medicinePort.updateMedicine(id, updateMedicineRequestDTO);
        // Convertir el medicamento a un DTO y retornarlo
        MedicineResponseDTO responseDTO = new MedicineResponseDTO(medicine);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteMethodName(@RequestParam Long id) {
        return ResponseEntity.ok().body(false);
    }

}
