package com.hospitalApi.patients.models;

import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "paciente")
public class Patient extends Auditor {

    @NotBlank(message = "El nombre del paciente es requerido")
    @Column(nullable = false, length = 250)
    private String firstnames;

    @NotBlank(message = "Los apellidos del paciente son requeridos")
    @Column(nullable = false, length = 250)
    private String lastnames;

    @NotBlank(message = "El número de DPI del paciente es requerido")
    @Pattern(regexp = "\\d{13}", message = "El número de DPI debe contener solo números y tener 13 dígitos")
    @Column(unique = true, nullable = false, length = 13)
    private String dpi;

    public Patient(String id, String firstnames, String lastnames, String dpi) {
        super(id);
        this.firstnames = firstnames;
        this.lastnames = lastnames;
        this.dpi = dpi;
    }

    public Patient(String firstnames, String lastnames, String dpi) {
        super();
        this.firstnames = firstnames;
        this.lastnames = lastnames;
        this.dpi = dpi;
    }

    public Patient updateFromDTO() {
        return this;
    }
}
