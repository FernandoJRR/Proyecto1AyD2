package com.hospitalApi.employees.mappers;

import java.time.LocalDate;

import org.mapstruct.Mapper;

import com.hospitalApi.employees.dtos.EmployeeHistoryDateRequestDTO;
import com.hospitalApi.employees.models.EmployeeHistory;

@Mapper(componentModel = "spring")
public interface EmployeeHistoryMapper {
    public EmployeeHistory fromEmployeeHistoryDateRequestDtoToEmployeeHistory(EmployeeHistoryDateRequestDTO dto);
}
