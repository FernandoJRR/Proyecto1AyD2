package com.hospitalApi.consults.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.consults.dtos.ConsultResponseDTO;
import com.hospitalApi.consults.dtos.CreateConsultRequestDTO;
import com.hospitalApi.consults.dtos.TotalConsultResponseDTO;
import com.hospitalApi.consults.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.consults.mappers.ConsultMapper;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.port.ForConsultPort;
import com.hospitalApi.shared.exceptions.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("api/v1/consults")
@RequiredArgsConstructor
public class ConsultController {

    private final ForConsultPort consultPort;
    private final ConsultMapper consultMapper;

    @Operation(summary = "Obtener todas las consultas", description = "Este endpoint devuelve una lista con todas las consultas registradas en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consultas obtenidas exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/all")
    public ResponseEntity<List<ConsultResponseDTO>> getAllConsults() {
        List<Consult> consults = consultPort.getAllConsults();
        return ResponseEntity.ok().body(consultMapper.fromConsultsToResponse(consults));
    }

    @Operation(summary = "Obtener una consulta por ID", description = "Este endpoint permite obtener una consulta por su identificador único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Consulta no encontrada", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ConsultResponseDTO> getConsult(
            @PathVariable @NotNull(message = "El id de la consulta no puede ser nulo") String id)
            throws NotFoundException {
        Consult consult = consultPort.findById(id);
        return ResponseEntity.ok().body(new ConsultResponseDTO(consult));
    }

    @Operation(summary = "Crear una nueva consulta", description = "Este endpoint permite registrar una nueva consulta para un paciente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Consulta creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida, error en los datos de entrada", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/create")
    public ResponseEntity<ConsultResponseDTO> createConsult(
            @RequestBody @Valid CreateConsultRequestDTO createConsultRequestDTO)
            throws NotFoundException {

        Consult consult = consultPort.createConsult(createConsultRequestDTO.getPatientId(),
                createConsultRequestDTO.getCostoConsulta());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ConsultResponseDTO(consult));
    }

    @Operation(summary = "Actualizar una consulta", description = "Este endpoint permite actualizar la información de una consulta existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta actualizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida, error en los datos de entrada", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Consulta no encontrada", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ConsultResponseDTO> updateConsult(
            @PathVariable @NotNull(message = "El id de la consulta no puede ser nulo") String id,
            @RequestBody @Valid UpdateConsultRequestDTO updateConsultRequestDTO)
            throws NotFoundException {
        Consult consult = consultPort.updateConsult(id, updateConsultRequestDTO);
        return ResponseEntity.ok().body(new ConsultResponseDTO(consult));
    }

    @Operation(summary = "Pagar una consulta", description = "Este endpoint permite pagar una consulta, cambiando su estado a pagada.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta pagada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Consulta no encontrada", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "409", description = "La consulta ya se encuentra pagada", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/pay/{id}")
    public ResponseEntity<ConsultResponseDTO> payConsult(
            @PathVariable @NotNull(message = "El id de la consulta no puede ser nulo") String id)
            throws NotFoundException, IllegalStateException {
        Consult consult = consultPort.pagarConsulta(id);
        return ResponseEntity.ok().body(new ConsultResponseDTO(consult));
    }

    @Operation(summary = "Obtener el total de una consulta", description = "Este endpoint devuelve el costo total de una consulta incluyendo posibles cirugías.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total obtenido exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class))),
        @ApiResponse(responseCode = "404", description = "Consulta no encontrada", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/total/{id}")
    public ResponseEntity<TotalConsultResponseDTO> getTotalConsult(
            @PathVariable @NotNull(message = "El id de la consulta no puede ser nulo") String id)
            throws NotFoundException {
        Double total = consultPort.obtenerTotalConsulta(id);
        return ResponseEntity.ok().body(new TotalConsultResponseDTO(id, total));
    }

}
