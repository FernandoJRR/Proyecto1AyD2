/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.hospitalApi.employees.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;

import com.hospitalApi.shared.models.Auditor;
import com.hospitalApi.users.models.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author Luis Monterroso
 */
@Entity(name = "employee")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@DynamicUpdate
public class Employee extends Auditor {

    @Column(length = 100)
    private String firstName;
    @Column(length = 100)
    private String lastName;
    @Column(scale = 2)
    private BigDecimal salary;
    @Column(precision = 5, scale = 2, nullable = true)
    private BigDecimal igssPercentage;
    @Column(precision = 5, scale = 2, nullable = true)
    private BigDecimal irtraPercentage;
    //@Column(nullable = true)
    //private LocalDateTime resignDate;
    @Column(nullable = true)
    private LocalDate desactivatedAt;

    @ManyToOne
    private EmployeeType employeeType;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private User user;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<EmployeeHistory> employeeHistories;

    /**
     * Para la creacion de nuevos empleados
     *
     * @param firstName
     * @param lastName
     * @param salary
     * @param igssPercentage
     * @param irtraPercentage
     */
    public Employee(String firstName, String lastName, BigDecimal salary, BigDecimal igssPercentage,
            BigDecimal irtraPercentage
            //LocalDateTime resignDate
            ) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.igssPercentage = igssPercentage;
        this.irtraPercentage = irtraPercentage;
        //this.resignDate = resignDate;
    }

    public Employee(String firstName, String lastName, BigDecimal salary, BigDecimal igssPercentage,
            BigDecimal irtraPercentage, LocalDateTime resignDate, EmployeeType employeeType, User user) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.igssPercentage = igssPercentage;
        this.irtraPercentage = irtraPercentage;
        //this.resignDate = resignDate;
        this.employeeType = employeeType;
        this.user = user;
    }

}
