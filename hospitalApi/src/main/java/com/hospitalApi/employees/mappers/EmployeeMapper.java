package com.hospitalApi.employees.mappers;

import java.math.BigDecimal;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hospitalApi.employees.dtos.CreateEmployeeRequestDTO;
import com.hospitalApi.employees.dtos.EmployeeRequestDTO;
import com.hospitalApi.employees.dtos.EmployeeResponseDTO;
import com.hospitalApi.employees.dtos.EmployeeSalaryRequestDTO;
import com.hospitalApi.employees.dtos.HistoryTypeResponseDTO;
import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.HistoryType;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    public Employee fromCreateEmployeeRequestDtoToEmployee(CreateEmployeeRequestDTO dto);

    public Employee fromEmployeeRequestDtoToEmployee(EmployeeRequestDTO dto);

    public EmployeeResponseDTO fromEmployeeToResponse(Employee employee);

    public List<EmployeeResponseDTO> fromEmployeesToResponse(List<Employee> employees);

}
