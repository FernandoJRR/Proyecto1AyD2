package com.hospitalApi.surgery.dtos;

import com.hospitalApi.surgery.models.SurgeryType;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SurgeryTypeResponseDTO {
    private String type;
    private String description;
    private Double specialistPayment;
    private Double hospitalCost;
    private Double surgeryCost;

    public SurgeryTypeResponseDTO(SurgeryType surgeryType) {
        this.type = surgeryType.getType();
        this.description = surgeryType.getDescription();
        this.specialistPayment = surgeryType.getSpecialistPayment();
        this.hospitalCost = surgeryType.getHospitalCost();
        this.surgeryCost = surgeryType.getSurgeryCost();
    }
}
