package com.hospitalApi.employees.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.employees.models.SpecialistEmployee;

public interface SpecialistEmployeeRepository extends JpaRepository<SpecialistEmployee, String> {
    public boolean existsByDpi(String dpi);

    public SpecialistEmployee findByDpi(String dpi);

    public SpecialistEmployee findByNombresAndApellidos(String nombres, String apellidos);
}
