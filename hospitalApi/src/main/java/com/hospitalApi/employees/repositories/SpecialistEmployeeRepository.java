package com.hospitalApi.employees.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.employees.models.SpecialistEmployee;

public interface SpecialistEmployeeRepository extends JpaRepository<SpecialistEmployee, String> {
    public boolean existsByDpi(String dpi);

    public SpecialistEmployee findByDpi(String dpi);

    public List<SpecialistEmployee> findByNombresAndApellidos(String nombres, String apellidos);
}
