package com.hospitalApi.employees.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.employees.models.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

}
