package com.hospitalApi.patients.models;

import java.util.List;

import com.hospitalApi.consult.models.Consult;
import com.hospitalApi.patients.dtos.CreatePatientRequestDTO;
import com.hospitalApi.patients.dtos.UpdatePatientRequestDTO;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "paciente")
public class Patient extends Auditor {

    @NotBlank(message = "El nombre del paciente es requerido")
    @Size(min = 1, max = 250, message = "El nombre del paciente debe tener entre 1 y 250 caracteres")
    @Column(nullable = false, length = 250)
    private String firstnames;

    @NotBlank(message = "Los apellidos del paciente son requeridos")
    @Size(min = 1, max = 250, message = "Los apellidos del paciente deben tener entre 1 y 250 caracteres")
    @Column(nullable = false, length = 250)
    private String lastnames;

    @NotBlank(message = "El número de DPI del paciente es requerido")
    @Pattern(regexp = "\\d{13}", message = "El número de DPI debe contener solo números y tener 13 dígitos")
    @Column(unique = true, nullable = false, length = 13)
    private String dpi;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consult> consultas;

    /**
     * Contructor de un paciente en base a su id, nombres, apellidos y dpi
     * 
     * @param id
     * @param firstnames
     * @param lastnames
     * @param dpi
     */
    public Patient(String id, String firstnames, String lastnames, String dpi) {
        super(id);
        this.firstnames = firstnames;
        this.lastnames = lastnames;
        this.dpi = dpi;
    }

    public Patient(CreatePatientRequestDTO createPatientRequestDTO) {
        this.firstnames = createPatientRequestDTO.getFirstnames();
        this.lastnames = createPatientRequestDTO.getLastnames();
        this.dpi = createPatientRequestDTO.getDpi();
    }

    /**
     * Contrictor de un paciente en base a sus nombres, apellidos y dpi
     * 
     * @param firstnames
     * @param lastnames
     * @param dpi
     */
    public Patient(String firstnames, String lastnames, String dpi) {
        this.firstnames = firstnames;
        this.lastnames = lastnames;
        this.dpi = dpi;
    }

    /**
     * Actualiza los datos del paciente con los datos del DTO
     * 
     * @param updatePatientRequestDTO
     * @return
     */
    public Patient updateFromDTO(UpdatePatientRequestDTO updatePatientRequestDTO) {
        if (updatePatientRequestDTO.getFirstnames() != null) {
            this.firstnames = updatePatientRequestDTO.getFirstnames();
        }
        if (updatePatientRequestDTO.getLastnames() != null) {
            this.lastnames = updatePatientRequestDTO.getLastnames();
        }
        if (updatePatientRequestDTO.getDpi() != null) {
            this.dpi = updatePatientRequestDTO.getDpi();
        }
        return this;
    }
}
