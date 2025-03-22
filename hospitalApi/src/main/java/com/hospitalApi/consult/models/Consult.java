package com.hospitalApi.consult.models;

import java.util.List;

import com.hospitalApi.consult.dtos.CreateConsultRequestDTO;
import com.hospitalApi.consult.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "consulta")
public class Consult extends Auditor {

    @NotBlank(message = "El paciente es requerido")
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @NotNull(message = "El estado de la consulta es requerido")
    private Boolean isInternado = false;

    @Column(nullable = false)
    @DecimalMin(value = "0.01", inclusive = true, message = "El costo de la consulta debe ser mayor a 0")
    private Double costoConsulta;

    @Column(nullable = false)
    @DecimalMin(value = "0.01", inclusive = true, message = "El costo de la medicina debe ser mayor a 0")
    private Double costoTotal;

    @OneToMany(mappedBy = "consult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleMedicine> saleMedicines;

    // Habitacion
    // private Habitacion habitacion;
    public Consult(String id, Patient patient, Boolean isInternado, Double costoConsulta, Double costoTotal) {
        super(id);
        this.patient = patient;
        this.isInternado = isInternado;
        this.costoConsulta = costoConsulta;
        this.costoTotal = costoTotal;
    }

    public Consult(CreateConsultRequestDTO createConsultRequestDTO, Patient patient) {
        this.patient = patient;
        this.costoConsulta = createConsultRequestDTO.getCostoConsulta();
        this.costoTotal = createConsultRequestDTO.getCostoConsulta();
    }

    public Consult updateConsultFromDTO(UpdateConsultRequestDTO updateConsultRequestDTO) {
        if (updateConsultRequestDTO.getIsInternado() != null) {
            this.isInternado = updateConsultRequestDTO.getIsInternado();
        }
        if (updateConsultRequestDTO.getCostoConsulta() != null) {
            this.costoConsulta = updateConsultRequestDTO.getCostoConsulta();
        }
        return this;
    }

}
