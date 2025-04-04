package com.hospitalApi.employees.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hospitalApi.employees.dtos.EmployeeHistoryDateRequestDTO;
import com.hospitalApi.employees.dtos.EmployeeHistoryResponseDTO;
import com.hospitalApi.employees.models.EmployeeHistory;
import com.hospitalApi.shared.utils.DateFormatterUtil;

@Mapper(componentModel = "spring", uses = { DateFormatterUtil.class })
public interface EmployeeHistoryMapper {
    public EmployeeHistory fromEmployeeHistoryDateRequestDtoToEmployeeHistory(EmployeeHistoryDateRequestDTO dto);

    @Mapping(source = "historyDate", target = "historyDate", qualifiedByName = "formatDateToLocalFormat")
    public EmployeeHistoryResponseDTO fromEmployeeHistoryToEmployeeHistoryDto(EmployeeHistory employeeHistory);

    public List<EmployeeHistoryResponseDTO> fromEmployeeHistoriesToEmployeeHistoryDtoList(
            List<EmployeeHistory> employeeHistories);
}
