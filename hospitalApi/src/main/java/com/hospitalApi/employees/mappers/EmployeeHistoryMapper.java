package com.hospitalApi.employees.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.hospitalApi.employees.dtos.EmployeeHistoryDateRequestDTO;
import com.hospitalApi.employees.dtos.EmployeeHistoryResponseDTO;
import com.hospitalApi.employees.models.EmployeeHistory;

@Mapper(componentModel = "spring")
public interface EmployeeHistoryMapper {
    public EmployeeHistory fromEmployeeHistoryDateRequestDtoToEmployeeHistory(EmployeeHistoryDateRequestDTO dto);

    public EmployeeHistoryResponseDTO fromEmployeeHistoryToEmployeeHistoryDto(EmployeeHistory employeeHistory);

    public List<EmployeeHistoryResponseDTO> fromEmployeeHistoriesToEmployeeHistoryDtoList(List<EmployeeHistory> employeeHistories);
}
