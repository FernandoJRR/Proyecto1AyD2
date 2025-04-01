package com.hospitalApi.parameters.models;

import java.time.LocalDate;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Parameter extends Auditor {
    @Column(unique = true)
    private String parameterKey;

    @Column
    private String value;

    @Column
    private String name;
}