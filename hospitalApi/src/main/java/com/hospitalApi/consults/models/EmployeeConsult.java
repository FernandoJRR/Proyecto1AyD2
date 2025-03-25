package com.hospitalApi.consults.models;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EmployeeConsult extends Auditor {

    @ManyToOne
    @JoinColumn(nullable = false)
    private Consult consult;

    @ManyToOne
    @JoinColumn( nullable = false)
    private Employee employee;
}