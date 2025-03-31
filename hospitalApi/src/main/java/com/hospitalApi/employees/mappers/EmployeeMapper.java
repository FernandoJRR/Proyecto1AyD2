package com.hospitalApi.employees.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.hospitalApi.employees.dtos.CreateEmployeeRequestDTO;
import com.hospitalApi.employees.dtos.EmployeeRequestDTO;
import com.hospitalApi.employees.dtos.EmployeeResponseDTO;
import com.hospitalApi.employees.models.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    public Employee fromCreateEmployeeRequestDtoToEmployee(CreateEmployeeRequestDTO dto);

    public Employee fromEmployeeRequestDtoToEmployee(EmployeeRequestDTO dto);

    public EmployeeResponseDTO fromEmployeeToResponse(Employee employee);

    public List<EmployeeResponseDTO> fromEmployeesToResponse(List<Employee> employees);

}
