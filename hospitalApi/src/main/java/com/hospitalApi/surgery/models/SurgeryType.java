package com.hospitalApi.surgery.models;

import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tipo_cirugia")
public class SurgeryType extends Auditor {

    @NotBlank(message = "El nombre del tipo de cirugía es requerido")
    @Size(min = 3, max = 100, message = "El nombre del tipo de cirugía debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
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

}
