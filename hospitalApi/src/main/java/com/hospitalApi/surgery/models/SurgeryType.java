package com.hospitalApi.surgery.models;

import com.hospitalApi.shared.models.Auditor;
import com.hospitalApi.surgery.dtos.CreateSurgeryTypeRequest;
import com.hospitalApi.surgery.dtos.UpdateSurgeryTypeRequestDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SurgeryType extends Auditor {

    @Column(nullable = false, length = 100, unique = true)
    private String type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private Double specialistPayment;

    @Column(nullable = false)
    private Double hospitalCost;

    @Column(nullable = false)
    private Double surgeryCost;

    public SurgeryType updateFromDTO(UpdateSurgeryTypeRequestDTO updateSurgeryTypeRequestDTO) {
        if (updateSurgeryTypeRequestDTO == null) {
            return this;
        }
        if (updateSurgeryTypeRequestDTO.getType() != null) {
            this.type = updateSurgeryTypeRequestDTO.getType();
        }
        if (updateSurgeryTypeRequestDTO.getDescription() != null) {
            this.description = updateSurgeryTypeRequestDTO.getDescription();
        }

        if (updateSurgeryTypeRequestDTO.getSpecialistPayment() != null) {
            this.specialistPayment = updateSurgeryTypeRequestDTO.getSpecialistPayment();
        }

        if (updateSurgeryTypeRequestDTO.getHospitalCost() != null) {
            this.hospitalCost = updateSurgeryTypeRequestDTO.getHospitalCost();
        }

        if (updateSurgeryTypeRequestDTO.getSurgeryCost() != null) {
            this.surgeryCost = updateSurgeryTypeRequestDTO.getSurgeryCost();
        }
        return this;
    }

    public SurgeryType(CreateSurgeryTypeRequest createSurgeryTypeRequestDTO) {
        this.type = createSurgeryTypeRequestDTO.getType();
        this.description = createSurgeryTypeRequestDTO.getDescription();
        this.specialistPayment = createSurgeryTypeRequestDTO.getSpecialistPayment();
        this.hospitalCost = createSurgeryTypeRequestDTO.getHospitalCost();
        this.surgeryCost = createSurgeryTypeRequestDTO.getSurgeryCost();
    }
}
