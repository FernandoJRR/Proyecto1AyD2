package com.hospitalApi.surgery.models;

import com.hospitalApi.shared.models.Auditor;
import com.hospitalApi.surgery.dtos.CreateSurgeryTypeRequest;
import com.hospitalApi.surgery.dtos.UpdateSurgeryTypeRequestDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "El nombre del tipo de cirugía es requerido")
    @Size(min = 3, max = 100, message = "El nombre del tipo de cirugía debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100, unique = true)
    private String type;

    @NotBlank(message = "La descripción del tipo de cirugía es requerida")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotBlank(message = "El pago al especialista es requerido")
    @DecimalMin(value = "0.01", message = "El pago al especialista debe ser mayor a 0")
    @Column(nullable = false)
    private Double specialistPayment;

    @NotBlank(message = "El costo del hospital es requerido")
    @DecimalMin(value = "0.01", message = "El costo del hospital debe ser mayor a 0")
    @Column(nullable = false)
    private Double hospitalCost;

    @NotBlank(message = "El costo de la cirugía es requerido")
    @DecimalMin(value = "0.01", message = "El costo de la cirugía debe ser mayor a 0")
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
