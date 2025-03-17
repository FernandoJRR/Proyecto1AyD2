package com.hospitalApi.employees.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.employees.models.EmployeeHistory;

public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, String>{

}
