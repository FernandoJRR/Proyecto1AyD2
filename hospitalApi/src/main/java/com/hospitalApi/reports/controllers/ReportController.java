package com.hospitalApi.reports.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.employees.dtos.EmployeeHistoryResponseDTO;
import com.hospitalApi.medicines.dtos.MedicineResponseDTO;
import com.hospitalApi.reports.dtos.request.DoctorAssignmentFilter;
import com.hospitalApi.reports.dtos.request.EmployeeLifecycleFilter;
import com.hospitalApi.reports.dtos.request.EmployeeProfitFilter;
import com.hospitalApi.reports.dtos.request.FinancialFilter;
import com.hospitalApi.reports.dtos.request.MedicationProfitFilter;
import com.hospitalApi.reports.dtos.request.MedicationReportFilter;
import com.hospitalApi.reports.dtos.response.doctorAssignmentReport.EmployeeAssignableResponseDTO;
import com.hospitalApi.reports.dtos.response.employeeSalesReport.EmployeeProfitSummary;
import com.hospitalApi.reports.dtos.response.financialReport.FinancialReportDTO;
import com.hospitalApi.reports.dtos.response.medicationProfitReport.MedicationProfitSummary;
import com.hospitalApi.reports.ports.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

        private final ReportService<List<MedicineResponseDTO>, MedicationReportFilter> medicationReportPort;
        private final ReportService<MedicationProfitSummary, MedicationProfitFilter> medicationProfitReportPort;
        private final ReportService<EmployeeProfitSummary, EmployeeProfitFilter> employeeProfitReportPort;
        private final ReportService<List<EmployeeHistoryResponseDTO>, EmployeeLifecycleFilter> employeeLifecycleReportPort;
        private final ReportService<List<EmployeeAssignableResponseDTO>, DoctorAssignmentFilter> doctorAssignmentReportPort;
        private final ReportService<FinancialReportDTO, FinancialFilter> financialReportPort;

        @Operation(summary = "Generar reporte de medicamentos.", description = "Obtiene un listado de medicamentos filtrados según los parámetros especificados.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reporte generado correctamente"),
                        @ApiResponse(responseCode = "403", description = "No tiene permisos para ver este reporte")
        })
        @GetMapping("/getMedicationReport")
        @PreAuthorize("hasAuthority('GET_MEDICATION_REPORT')")
        @ResponseStatus(HttpStatus.OK)
        public List<MedicineResponseDTO> getMedicinesReport(
                        @ModelAttribute MedicationReportFilter filter) {
                List<MedicineResponseDTO> medicinesDTO = medicationReportPort.generateReport(filter);
                return medicinesDTO;
        }

        @Operation(summary = "Generar reporte de ganancias por medicamento.", description = "Obtiene un resumen de ganancias por medicamento en el rango de fechas especificado.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reporte generado correctamente"),
                        @ApiResponse(responseCode = "403", description = "No tiene permisos para ver este reporte")
        })
        @GetMapping("/getMedicationProfitReport")
        @PreAuthorize("hasAuthority('GET_MEDICATION_PROFIT_REPORT')")
        @ResponseStatus(HttpStatus.OK)
        public MedicationProfitSummary getMedicationProfitReport(
                        @ModelAttribute MedicationProfitFilter filter) {
                MedicationProfitSummary report = medicationProfitReportPort
                                .generateReport(filter);
                return report;
        }

        @Operation(summary = "Generar reporte de ganancias por empleado.", description = "Obtiene un resumen de ganancias generadas por cada empleado.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reporte generado correctamente"),
                        @ApiResponse(responseCode = "403", description = "No tiene permisos para ver este reporte")
        })
        @GetMapping("/getEmployeeProfitReport")
        @PreAuthorize("hasAuthority('GET_EMPLOYEE_PROFIT_REPORT')")
        @ResponseStatus(HttpStatus.OK)
        public EmployeeProfitSummary getEmployeeProfitReport(
                        @ModelAttribute EmployeeProfitFilter filter) {
                EmployeeProfitSummary report = employeeProfitReportPort
                                .generateReport(filter);
                return report;
        }

        @Operation(summary = "Generar reporte de movimientos de empleados.", description = "Obtiene un historial de los movimientos de los empleados (altas, bajas, cambios).")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reporte generado correctamente"),
                        @ApiResponse(responseCode = "403", description = "No tiene permisos para ver este reporte")
        })
        @GetMapping("/getEmployeeLifecycleReport")
        @PreAuthorize("hasAuthority('GET_EMPLOYEE_LIFECYCLE_REPORT')")
        @ResponseStatus(HttpStatus.OK)
        public List<EmployeeHistoryResponseDTO> getEmployeeLifecycleReport(
                        @ModelAttribute EmployeeLifecycleFilter filter) {

                List<EmployeeHistoryResponseDTO> report = employeeLifecycleReportPort
                                .generateReport(filter);
                return report;
        }

        @Operation(summary = "Generar reporte de asignación de doctores.", description = "Obtiene un listado de doctores asignables a procedimientos según los filtros definidos.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reporte generado correctamente"),
                        @ApiResponse(responseCode = "403", description = "No tiene permisos para ver este reporte")
        })
        @GetMapping("/getDoctorAssignmentReport")
        @PreAuthorize("hasAuthority('GET_DOCTOR_ASSIGNMENT_REPORT')")
        @ResponseStatus(HttpStatus.OK)
        public List<EmployeeAssignableResponseDTO> getDoctorAssignmentReport(
                        @Valid @ModelAttribute DoctorAssignmentFilter filter) {
                List<EmployeeAssignableResponseDTO> report = doctorAssignmentReportPort
                                .generateReport(filter);
                return report;
        }

        @Operation(summary = "Generar reporte financiero.", description = "Obtiene un reporte financiero con resumen de ingresos, egresos y ganancias por área.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reporte generado correctamente"),
                        @ApiResponse(responseCode = "403", description = "No tiene permisos para ver este reporte")
        })
        @GetMapping("/getFinancialReport")
        @PreAuthorize("hasAuthority('GET_FINANCIAL_REPORT')")
        @ResponseStatus(HttpStatus.OK)
        public FinancialReportDTO getFinancialReport(
                        @Valid @ModelAttribute FinancialFilter filter) {
                FinancialReportDTO report = financialReportPort
                                .generateReport(filter);
                return report;
        }

}
