package com.hospitalApi.consults.models;

import java.util.List;

import com.hospitalApi.consults.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.rooms.models.RoomUsage;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Entity
public class Consult extends Auditor {

    @ManyToOne
    @JoinColumn
    private Patient patient;

    @NotNull(message = "El estado de la consulta es requerido")
    private Boolean isInternado = false;

    @Column(nullable = false)
    @DecimalMin(value = "0.01", inclusive = true, message = "El costo de la consulta debe ser mayor a 0")
    private Double costoConsulta;

    @Column(nullable = false)
    @DecimalMin(value = "0.01", inclusive = true, message = "El costo de la medicina debe ser mayor a 0")
    private Double costoTotal;

    @NotNull(message = "El estado de la consulta es requerido")
    private Boolean isPaid = false;

    @OneToMany(mappedBy = "consult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleMedicine> saleMedicines;

    @OneToMany(mappedBy = "consult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeConsult> employeeConsults;

    @OneToOne(mappedBy = "consult", cascade = CascadeType.ALL, optional = true)
    @JoinColumn(nullable = true)
    private RoomUsage roomUsage;

    // Habitacion
    // private Habitacion habitacion;
    public Consult(String id, Patient patient, Boolean isInternado, Double costoConsulta, Double costoTotal) {
        super(id);
        this.patient = patient;
        this.isInternado = isInternado;
        this.costoConsulta = costoConsulta;
        this.costoTotal = costoTotal;
    }

    public Consult(Patient patient, Double costoConsulta) {
        this.patient = patient;
        this.costoConsulta = costoConsulta;
        this.costoTotal = costoConsulta;
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
