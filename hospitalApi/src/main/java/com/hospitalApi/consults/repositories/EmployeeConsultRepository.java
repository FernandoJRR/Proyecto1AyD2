package com.hospitalApi.consults.repositories;

import com.hospitalApi.consults.models.EmployeeConsult;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeConsultRepository extends JpaRepository<EmployeeConsult, String> {
    public boolean existsByConsultIdAndEmployeeId(String consultId, String employeeId);

    public int countByConsultId(String consultId);

    public List<EmployeeConsult> findByConsultId(String consultId);

    public List<EmployeeConsult> findByEmployeeId(String employeeId);

    public void deleteByConsultIdAndEmployeeId(String consultId, String employeeId);
}
