package com.hospitalApi.medicines.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.medicines.dtos.CreateSaleMedicineConsultRequestDTO;
import com.hospitalApi.medicines.dtos.CreateSaleMedicineFarmaciaRequestDTO;
import com.hospitalApi.medicines.dtos.SaleMedicineResponseDTO;
import com.hospitalApi.medicines.mappers.SaleMedicineMapper;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.medicines.ports.ForSaleMedicinePort;
import com.hospitalApi.shared.exceptions.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sale-medicines")
@RequiredArgsConstructor
public class SaleMedicineController {

        private final ForSaleMedicinePort saleMedicinePort;
        private final SaleMedicineMapper saleMedicineMapper;

        @Operation(summary = "Realizar una venta de medicamento en farmacia", description = "Realiza una venta de medicamento en farmacia.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Venta de medicamento en farmacia realizada exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Error en los datos de la venta de medicamento en farmacia"),
                        @ApiResponse(responseCode = "404", description = "Venta de medicamento en farmacia no encontrada"),
        })
        @PostMapping("/farmacia")
        @PreAuthorize("hasAuthority('CREATE_SALE_MEDICINE_FARMACIA')")
        @ResponseStatus(HttpStatus.CREATED)
        public SaleMedicineResponseDTO ventaFarmacia(
                        @RequestBody @Valid CreateSaleMedicineFarmaciaRequestDTO createSaleMedicineFarmaciaRequestDTO)
                        throws NotFoundException {
                SaleMedicine medicine = saleMedicinePort.createSaleMedicine(
                                createSaleMedicineFarmaciaRequestDTO.getMedicineId(),
                                createSaleMedicineFarmaciaRequestDTO.getQuantity());
                // Convertimos el sale de la medicina a un DTO
                SaleMedicineResponseDTO saleMedicineDTO = saleMedicineMapper.fromMedicineSaleToSaleMedicineDTO(medicine);
                return saleMedicineDTO;
        }

        @Operation(summary = "Realizar varias ventas de medicamento en farmacia", description = "Realiza varias ventas de medicamento en farmacia.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Ventas de medicamento en farmacia realizadas exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Error en los datos de las ventas de medicamento en farmacia"),
                        @ApiResponse(responseCode = "404", description = "Ventas de medicamento en farmacia no encontradas"),
        })
        @PostMapping("/farmacia/varios")
        @ResponseStatus(code = HttpStatus.CREATED)
        public List<SaleMedicineResponseDTO> ventaFarmaciaVarios(
                        @RequestBody List<@Valid CreateSaleMedicineFarmaciaRequestDTO> createSaleMedicineFarmaciaRequestDTOs)
                        throws NotFoundException {
                List<SaleMedicine> medicines = saleMedicinePort
                                .createSaleMedicines(createSaleMedicineFarmaciaRequestDTOs);
                List<SaleMedicineResponseDTO> saleMedicineDTOs = saleMedicineMapper
                                .fromSaleMedicineListToSaleMedicineDTOList(medicines);
                return saleMedicineDTOs;
        }

        @Operation(summary = "Realizar una venta de medicamento en consultorio", description = "Realiza una venta de medicamento en consultorio.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Venta de medicamento en consultorio realizada exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Error en los datos de la venta de medicamento en consultorio"),
                        @ApiResponse(responseCode = "404", description = "Venta de medicamento en consultorio no encontrada"),
        })
        @PostMapping("/consult")
        @PreAuthorize("hasAuthority('CREATE_SALE_MEDICINE_CONSULT')")
        @ResponseStatus(HttpStatus.CREATED)
        public SaleMedicineResponseDTO ventaConsultorio(
                        @RequestBody @Valid CreateSaleMedicineConsultRequestDTO createSaleMedicineConsultRequestDTO)
                        throws NotFoundException {
                SaleMedicine medicine = saleMedicinePort.createSaleMedicine(
                                createSaleMedicineConsultRequestDTO.getConsultId(),
                                createSaleMedicineConsultRequestDTO.getMedicineId(),
                                createSaleMedicineConsultRequestDTO.getQuantity());
                // Convertimos el sale de la medicina a un DTO
                SaleMedicineResponseDTO saleMedicineDTO = saleMedicineMapper.fromMedicineSaleToSaleMedicineDTO(medicine);
                return saleMedicineDTO;
        }

        @Operation(summary = "Realizar varias ventas de medicamento en consultorio", description = "Realiza varias ventas de medicamento en consultorio.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Ventas de medicamento en consultorio realizadas exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Error en los datos de las ventas de medicamento en consultorio"),
                        @ApiResponse(responseCode = "404", description = "Ventas de medicamento en consultorio no encontradas"),
        })
        @PostMapping("/consult/varios")
        @ResponseStatus(code = HttpStatus.CREATED)
        public List<SaleMedicineResponseDTO> ventaConsultorioVarios(
                        @RequestBody List<@Valid CreateSaleMedicineConsultRequestDTO> createSaleMedicineConsultRequestDTOs)
                        throws NotFoundException {
                List<SaleMedicine> medicines = saleMedicinePort
                                .createSaleMedicinesForConsult(createSaleMedicineConsultRequestDTOs);
                List<SaleMedicineResponseDTO> saleMedicineDTOs = saleMedicineMapper
                                .fromSaleMedicineListToSaleMedicineDTOList(medicines);
                return saleMedicineDTOs;
        }

        @Operation(summary = "Obtener una venta de medicamento", description = "Obtiene una venta de medicamento.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Venta de medicamento obtenida exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Venta de medicamento no encontrada"),
        })
        @GetMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        public SaleMedicineResponseDTO getSaleMedicine(
                        @PathVariable("id") @NotBlank(message = "El id de la venta de medicamento es requerido") String id)
                        throws NotFoundException {
                SaleMedicine saleMedicine = saleMedicinePort.findById(id);
                SaleMedicineResponseDTO saleMedicineDTO = saleMedicineMapper.fromMedicineSaleToSaleMedicineDTO(saleMedicine);
                return saleMedicineDTO;
        }

}
