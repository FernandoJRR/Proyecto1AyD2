package com.hospitalApi.surgery.dtos;

import java.util.List;

import com.hospitalApi.surgery.models.Surgery;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
public class SurgeryResponseDTO {
    private String id;
    private String consultId;
    private Double hospitalCost;
    private Double surgeryCost;
    private SurgeryTypeResponseDTO surgeryType;
    private List<SurgeryEmpleoyeeResponseDTO> surgeryEmployees;

    public SurgeryResponseDTO(Surgery surgery, SurgeryTypeResponseDTO surgeryType,
            List<SurgeryEmpleoyeeResponseDTO> surgeryEmployees) {
        this.id = surgery.getId();
        this.consultId = surgery.getConsult().getId();
        this.hospitalCost = surgery.getHospitalCost();
        this.surgeryCost = surgery.getSurgeryCost();
        this.surgeryType = surgeryType;
        this.surgeryEmployees = surgeryEmployees;
    }
}
