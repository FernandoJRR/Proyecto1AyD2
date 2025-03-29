package com.hospitalApi.surgery.dtos;

import java.time.LocalDate;
import java.util.List;

import com.hospitalApi.surgery.models.Surgery;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Value;

@AllArgsConstructor
@Setter
@Value
public class SurgeryResponseDTO {
    private String id;
    private String consultId;
    private Double hospitalCost;
    private Double surgeryCost;
    private LocalDate performedDate;
    private SurgeryTypeResponseDTO surgeryType;
    private List<SurgeryEmpleoyeeResponseDTO> surgeryEmployees;

    public SurgeryResponseDTO(Surgery surgery, SurgeryTypeResponseDTO surgeryType,
            List<SurgeryEmpleoyeeResponseDTO> surgeryEmployees) {
        this.id = surgery.getId();
        this.consultId = surgery.getConsult().getId();
        this.hospitalCost = surgery.getHospitalCost();
        this.surgeryCost = surgery.getSurgeryCost();
        this.performedDate = surgery.getPerformedDate();
        this.surgeryType = surgeryType;
        this.surgeryEmployees = surgeryEmployees;
    }
}
