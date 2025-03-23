/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.hospitalApi.users.models;

import java.time.LocalDate;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity(name = "user")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends Auditor {

    @Column(unique = true, length = 100)
    private String username;

    @Column(length = 255)
    private String password;

    private LocalDate desactivatedAt;

    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String id, String username, String password) {
        super(id);
        this.username = username;
        this.password = password;
    }

}
