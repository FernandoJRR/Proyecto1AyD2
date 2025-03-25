package com.hospitalApi.surgery.dtos;

import com.hospitalApi.surgery.models.SurgeryType;

import lombok.Value;

@Value
public class SurgeryTypeResponseDTO {
    private String id;
    private String type;
    private String description;
    private Double specialistPayment;
    private Double hospitalCost;
    private Double surgeryCost;

    public SurgeryTypeResponseDTO(SurgeryType surgeryType) {
        this.id = surgeryType.getId();
        this.type = surgeryType.getType();
        this.description = surgeryType.getDescription();
        this.specialistPayment = surgeryType.getSpecialistPayment();
        this.hospitalCost = surgeryType.getHospitalCost();
        this.surgeryCost = surgeryType.getSurgeryCost();
    }
}
