package com.hospitalApi.employees.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.employees.models.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    List<Employee> findByEmployeeTypeId(String employeeTypeId);
}
