package com.hospitalApi.employees.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.employees.models.EmployeeType;

public interface EmployeeTypeRepository extends JpaRepository<EmployeeType, String> {
    /**
     * Verifica si el nombre del tipo de empleado existe en la bd
     * 
     * @param name
     * @return
     */
    public boolean existsByName(String name);
}
