package com.hospitalApi.surgery.models;

import java.util.List;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "cirugia")
public class Surgery extends Auditor {

    @NotBlank(message = "La consulta es requerida")
    @OneToOne
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consult consult;

    @NotBlank(message = "El tipo de cirugía es requerido")
    @OneToOne
    @JoinColumn(name = "tipo_cirugia_id", nullable = false)
    private SurgeryType surgeryType;

    @NotBlank(message = "El pago al especialista es requerido")
    @DecimalMin(value = "0.01", message = "El pago al especialista debe ser mayor a 0")
    @Column(nullable = false)
    private Double hospitalCost;

    @NotBlank(message = "El costo de la cirugía es requerido")
    @DecimalMin(value = "0.01", message = "El costo de la cirugía debe ser mayor a 0")
    @Column(nullable = false)
    private Double surgeryCost;

    @OneToMany(mappedBy = "surgery")
    private List<SurgeryEmployee> surgeryEmployees;

    public Surgery(Consult consult, SurgeryType surgeryType, Double hospitalCost, Double surgeryCost) {
        this.consult = consult;
        this.surgeryType = surgeryType;
        this.hospitalCost = hospitalCost;
        this.surgeryCost = surgeryCost;
    }

}
