package com.hospitalApi.vacations.models;

import java.time.LocalDate;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Vacations extends Auditor {
    @Column
    private Integer periodo_anio;

    @ManyToOne
    @JoinColumn
    private Employee employee;

    @Column
    private LocalDate fechaInicio;

    @Column
    private LocalDate fechaFin;

    @Column
    private Boolean se_utilizo;
}