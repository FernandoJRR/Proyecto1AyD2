package com.hospitalApi.medicines.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.medicines.dtos.CreateSaleMedicineConsultRequestDTO;
import com.hospitalApi.medicines.dtos.CreateSaleMedicineFarmaciaRequestDTO;
import com.hospitalApi.medicines.dtos.SaleMedicineDTO;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.medicines.ports.ForSaleMedicinePort;
import com.hospitalApi.shared.exceptions.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/sale-medicines")
@RequiredArgsConstructor
public class SaleMedicineController {

    private final ForSaleMedicinePort saleMedicinePort;

    @Operation(summary = "Realizar una venta de medicamento en farmacia", description = "Realiza una venta de medicamento en farmacia.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta de medicamento en farmacia realizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleMedicineDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error en los datos de la venta de medicamento en farmacia", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Venta de medicamento en farmacia no encontrada", content = @Content(mediaType = "application/json")),
    })
    @PostMapping("/farmacia")
    public ResponseEntity<SaleMedicineDTO> ventaFarmacia(
            @RequestBody @Valid CreateSaleMedicineFarmaciaRequestDTO createSaleMedicineFarmaciaRequestDTO)
            throws NotFoundException {
        SaleMedicine medicine = saleMedicinePort.createSaleMedicine(
                createSaleMedicineFarmaciaRequestDTO.getMedicineId(),
                createSaleMedicineFarmaciaRequestDTO.getQuantity());
        // Convertimos el sale de la medicina a un DTO
        SaleMedicineDTO saleMedicineDTO = new SaleMedicineDTO(medicine);
        return ResponseEntity.status(HttpStatus.CREATED).body(saleMedicineDTO);
    }


    @Operation(summary = "Realizar una venta de medicamento en consultorio", description = "Realiza una venta de medicamento en consultorio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta de medicamento en consultorio realizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleMedicineDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error en los datos de la venta de medicamento en consultorio", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Venta de medicamento en consultorio no encontrada", content = @Content(mediaType = "application/json")),
    })
    @PostMapping("/consult")
    public ResponseEntity<SaleMedicineDTO> ventaConsultorio(
            @RequestBody @Valid CreateSaleMedicineConsultRequestDTO createSaleMedicineConsultRequestDTO)
            throws NotFoundException {
        SaleMedicine medicine = saleMedicinePort.createSaleMedicine(
                createSaleMedicineConsultRequestDTO.getConsultId(),
                createSaleMedicineConsultRequestDTO.getMedicineId(),
                createSaleMedicineConsultRequestDTO.getQuantity());
        // Convertimos el sale de la medicina a un DTO
        SaleMedicineDTO saleMedicineDTO = new SaleMedicineDTO(medicine);
        return ResponseEntity.status(HttpStatus.CREATED).body(saleMedicineDTO);
    }

    @Operation(summary = "Obtener una venta de medicamento", description = "Obtiene una venta de medicamento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta de medicamento obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleMedicineDTO.class))),
            @ApiResponse(responseCode = "404", description = "Venta de medicamento no encontrada", content = @Content(mediaType = "application/json")),
    })
    @GetMapping("/{id}")
    public ResponseEntity<SaleMedicineDTO> getSaleMedicine(
            @RequestParam("id") @NotBlank(message = "El id de la venta de medicamento es requerido") String id)
            throws NotFoundException {
        SaleMedicine saleMedicine = saleMedicinePort.findById(id);
        SaleMedicineDTO saleMedicineDTO = new SaleMedicineDTO(saleMedicine);
        return ResponseEntity.ok().body(saleMedicineDTO);
    }

}
