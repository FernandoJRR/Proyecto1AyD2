package com.hospitalApi.employees.models;

import org.hibernate.annotations.DynamicUpdate;

import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity(name = "employeeHistory")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@DynamicUpdate
public class EmployeeHistory extends Auditor {
    @OneToOne(mappedBy = "employeeHistory")
    private HistoryType historyType;

    @ManyToOne
    private Employee employee;

    @Column(length = 200)
    private String commentary;
}
