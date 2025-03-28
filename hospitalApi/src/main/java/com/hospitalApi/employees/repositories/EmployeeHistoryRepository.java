package com.hospitalApi.employees.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.employees.models.EmployeeHistory;

public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, String>{
    List<EmployeeHistory> findAllByEmployee_IdOrderByHistoryDateAsc(String employeeId);

    Optional<EmployeeHistory> findFirstByEmployee_IdAndHistoryType_IdInOrderByHistoryDateDesc(String employeeId, List<String> historyTypeIds);

    Optional<EmployeeHistory> findFirstByEmployee_IdAndHistoryType_IdInAndHistoryDateLessThanEqualOrderByHistoryDateDesc(

    String employeeId, List<String> historyTypeIds, LocalDate untilDate);

    Optional<EmployeeHistory> findFirstByEmployee_IdOrderByHistoryDateAsc(String employeeId);

    List<EmployeeHistory> findByEmployee_IdAndHistoryType_TypeInOrderByHistoryDateAsc(String employeeId, List<String> historyTypeTypes);

    Optional<EmployeeHistory> findFirstByEmployee_IdAndHistoryType_TypeInOrderByHistoryDateDesc(String employeeId, List<String> historyTypes);
}
