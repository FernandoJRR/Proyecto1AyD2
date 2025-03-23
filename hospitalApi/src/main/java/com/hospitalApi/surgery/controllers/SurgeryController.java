package com.hospitalApi.surgery.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.dtos.AddDeleteEmployeeSurgeryDTO;
import com.hospitalApi.surgery.dtos.CreateSugeryRequestDTO;
import com.hospitalApi.surgery.dtos.CreateSurgeryTypeRequest;
import com.hospitalApi.surgery.dtos.SurgeryEmpleoyeeResponseDTO;
import com.hospitalApi.surgery.dtos.SurgeryResponseDTO;
import com.hospitalApi.surgery.dtos.SurgeryTypeResponseDTO;
import com.hospitalApi.surgery.mappers.SurgeryEmployeeMapper;
import com.hospitalApi.surgery.mappers.SurgeryTypeMapper;
import com.hospitalApi.surgery.models.Surgery;
import com.hospitalApi.surgery.models.SurgeryEmployee;
import com.hospitalApi.surgery.models.SurgeryType;
import com.hospitalApi.surgery.ports.ForSurgeryEmployeePort;
import com.hospitalApi.surgery.ports.ForSurgeryPort;
import com.hospitalApi.surgery.ports.ForSurgeryTypePort;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.websocket.server.PathParam;
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

    @GetMapping("/types/all")
    public ResponseEntity<List<SurgeryTypeResponseDTO>> getAllSurgeryTypes(
            @RequestParam(value = "query", required = false) String query) {
        List<SurgeryType> response = surgeryTypePort.getSurgeryTypes(query);
        List<SurgeryTypeResponseDTO> surgeryTypeResponseDTOList = surgeryTypeMapper
                .fromSurgeryTypeListToSurgeryTypeResponseDTOList(response);
        return ResponseEntity.ok().body(surgeryTypeResponseDTOList);
    }

    @PostMapping("/types/create")
    public ResponseEntity<SurgeryTypeResponseDTO> createSurgeryType(
            @Valid @RequestBody CreateSurgeryTypeRequest createSurgeryTypeRequestDTO) throws DuplicatedEntryException {
        SurgeryType surgeryType = new SurgeryType(createSurgeryTypeRequestDTO);
        surgeryType = surgeryTypePort.createSurgeryType(surgeryType);
        SurgeryTypeResponseDTO response = new SurgeryTypeResponseDTO(surgeryType);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/types/{surgeryTypeId}")
    public ResponseEntity<SurgeryTypeResponseDTO> getSurgeryType(
            @PathParam("surgeryTypeId") @NotBlank(message = "El id del tipo de cirugía es requerido") String surgeryTypeId)
            throws NotFoundException {
        SurgeryType surgeryType = surgeryTypePort.getSurgeryType(surgeryTypeId);
        SurgeryTypeResponseDTO response = new SurgeryTypeResponseDTO(surgeryType);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SurgeryResponseDTO>> getAllSurgeries() {
        List<Surgery> surgeryList = surgeryPort.getSurgerys();
        List<SurgeryResponseDTO> response = surgeryList.stream().map(surgery -> {
            SurgeryTypeResponseDTO surgeryType = new SurgeryTypeResponseDTO(surgery.getSurgeryType());
            List<SurgeryEmpleoyeeResponseDTO> surgeryEmployees = surgeryEmployeeMapper
                    .fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(surgery.getSurgeryEmployees());
            return new SurgeryResponseDTO(surgery, surgeryType, surgeryEmployees);
        }).toList();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/create")
    public ResponseEntity<SurgeryResponseDTO> createSurgery(
            @Valid @RequestBody CreateSugeryRequestDTO createSurgeryRequestDTO) throws NotFoundException {
        Surgery surgery = surgeryPort.createSurgery(createSurgeryRequestDTO.getConsultId(),
                createSurgeryRequestDTO.getSurgeryTypeId());
        SurgeryTypeResponseDTO surgeryType = new SurgeryTypeResponseDTO(surgery.getSurgeryType());
        List<SurgeryEmpleoyeeResponseDTO> surgeryEmployees = surgeryEmployeeMapper
                .fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(surgery.getSurgeryEmployees());
        SurgeryResponseDTO response = new SurgeryResponseDTO(surgery, surgeryType, surgeryEmployees);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("{surgeryId}")
    public ResponseEntity<SurgeryResponseDTO> getSurgery(
            @PathParam("surgeryId") @NotBlank(message = "El id de la cirugía es requerido") String surgeryId)
            throws NotFoundException {
        Surgery surgery = surgeryPort.getSurgery(surgeryId);
        SurgeryTypeResponseDTO surgeryType = new SurgeryTypeResponseDTO(surgery.getSurgeryType());
        List<SurgeryEmpleoyeeResponseDTO> surgeryEmployees = surgeryEmployeeMapper
                .fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(surgery.getSurgeryEmployees());
        SurgeryResponseDTO response = new SurgeryResponseDTO(surgery, surgeryType, surgeryEmployees);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("add-employee")
    public ResponseEntity<List<SurgeryEmpleoyeeResponseDTO>> addEmployeeToSurgery(
            @RequestBody @Valid AddDeleteEmployeeSurgeryDTO addEmployeeSurgeryDTO)
            throws NotFoundException, DuplicatedEntryException {
        List<SurgeryEmployee> response = surgeryEmployeePort
                .addEmpleoyeeToSurgery(addEmployeeSurgeryDTO.getSurgeryId(), addEmployeeSurgeryDTO.getEmployeeId());
        List<SurgeryEmpleoyeeResponseDTO> surgeryEmpleoyeeResponseDTOList = surgeryEmployeeMapper
                .fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(response);
        return ResponseEntity.ok().body(surgeryEmpleoyeeResponseDTOList);
    }

    @DeleteMapping("remove-employee")
    public ResponseEntity<List<SurgeryEmpleoyeeResponseDTO>> removeEmployeeFromSurgery(
            @RequestBody @Valid AddDeleteEmployeeSurgeryDTO removeEmployeeSurgeryDTO) throws NotFoundException {
        List<SurgeryEmployee> response = surgeryEmployeePort
                .removeEmployeeFromSurgery(removeEmployeeSurgeryDTO.getSurgeryId(),
                        removeEmployeeSurgeryDTO.getEmployeeId());
        List<SurgeryEmpleoyeeResponseDTO> surgeryEmpleoyeeResponseDTOList = surgeryEmployeeMapper
                .fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(response);
        return ResponseEntity.ok().body(surgeryEmpleoyeeResponseDTOList);
    }

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

    @DeleteMapping("remove-specialist")
    public ResponseEntity<List<SurgeryEmpleoyeeResponseDTO>> removeSpecialistFromSurgery(
            @RequestBody @Valid AddDeleteEmployeeSurgeryDTO removeSpecialistSurgeryDTO) throws NotFoundException {
        List<SurgeryEmployee> response = surgeryEmployeePort
                .removeSpecialistFromSurgery(removeSpecialistSurgeryDTO.getSurgeryId(),
                        removeSpecialistSurgeryDTO.getEmployeeId());
        List<SurgeryEmpleoyeeResponseDTO> surgeryEmpleoyeeResponseDTOList = surgeryEmployeeMapper
                .fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(response);
        return ResponseEntity.ok().body(surgeryEmpleoyeeResponseDTOList);
    }

    @GetMapping("/surgery-employees/{surgeryId}")
    public ResponseEntity<List<SurgeryEmpleoyeeResponseDTO>> getSurgeryEmployees(
            @PathParam("surgeryId") @NotBlank(message = "El id de la cirugía es requerido") String surgeryId)
            throws NotFoundException {
        List<SurgeryEmployee> response = surgeryEmployeePort.getSurgeryEmployees(surgeryId);
        List<SurgeryEmpleoyeeResponseDTO> surgeryEmpleoyeeResponseDTOList = surgeryEmployeeMapper
                .fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(response);
        return ResponseEntity.ok().body(surgeryEmpleoyeeResponseDTOList);
    }

}
