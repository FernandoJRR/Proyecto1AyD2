package com.hospitalApi.surgery.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.surgery.models.SurgeryEmployee;

public interface SurgeryEmployeeRepository extends JpaRepository<SurgeryEmployee, String> {
    public boolean existsBySurgeryIdAndEmployeeId(String surgeryId, String employeeId);

    public int deleteBySurgeryIdAndEmployeeId(String surgeryId, String employeeId);

    public boolean existsBySurgeryIdAndSpecialistEmployeeId(String surgeryId, String specialistEmployeeId);

    public int deleteBySurgeryIdAndSpecialistEmployeeId(String surgeryId, String specialistEmployeeId);

    public List<SurgeryEmployee> findBySurgeryId(String surgeryId);
}
