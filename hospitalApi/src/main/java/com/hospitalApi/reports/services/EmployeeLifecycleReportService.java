package com.hospitalApi.reports.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.employees.dtos.EmployeeHistoryResponseDTO;
import com.hospitalApi.employees.mappers.EmployeeHistoryMapper;
import com.hospitalApi.employees.models.EmployeeHistory;
import com.hospitalApi.employees.ports.ForEmployeeHistoryPort;
import com.hospitalApi.reports.dtos.request.EmployeeLifecycleFilter;
import com.hospitalApi.reports.ports.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeLifecycleReportService
        implements ReportService<List<EmployeeHistoryResponseDTO>, EmployeeLifecycleFilter> {

    private final ForEmployeeHistoryPort forEmployeeHistoryPort;
    private final EmployeeHistoryMapper employeeHistoryMapper;

    @Override
    public List<EmployeeHistoryResponseDTO> generateReport(EmployeeLifecycleFilter filter) {
        // mandamos a cargar los empleados que han sido despedidos o contratados
        // dependiendo del filtro
        List<EmployeeHistory> histories = forEmployeeHistoryPort
                .getHistoriesByHistoryDateBetweenAndEmployeeTypeIdAndHistoryTypeIds(
                        filter.getStartDate(), filter.getEndDate(),
                        filter.getEmployeeTypeId(), filter.getHistoryTypeIds());
        // construimos la history a dto
        List<EmployeeHistoryResponseDTO> dtos = employeeHistoryMapper
                .fromEmployeeHistoriesToEmployeeHistoryDtoList(histories);
        // retornamos
        return dtos;
    }

}
