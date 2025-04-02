package com.hospitalApi.reports.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
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
import com.hospitalApi.reports.dtos.request.MedicationProfitFilter;
import com.hospitalApi.reports.dtos.request.MedicationReportFilter;
import com.hospitalApi.reports.dtos.response.doctorAssignmentReport.EmployeeAssignableResponseDTO;
import com.hospitalApi.reports.dtos.response.employeeSalesReport.EmployeeProfitSummary;
import com.hospitalApi.reports.dtos.response.medicationProfitReport.MedicationProfitSummary;
import com.hospitalApi.reports.ports.ReportService;

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

        

        @GetMapping("/getMedicationReport")
        @ResponseStatus(HttpStatus.OK)
        public List<MedicineResponseDTO> getMedicinesReport(
                        @ModelAttribute MedicationReportFilter filter) {
                List<MedicineResponseDTO> medicinesDTO = medicationReportPort.generateReport(filter);
                return medicinesDTO;
        }

        @GetMapping("/getMedicationProfitReport")
        @ResponseStatus(HttpStatus.OK)
        public MedicationProfitSummary getMedicationProfitReport(
                        @ModelAttribute MedicationProfitFilter filter) {
                MedicationProfitSummary report = medicationProfitReportPort
                                .generateReport(filter);
                return report;
        }

        @GetMapping("/getEmployeeProfitReport")
        @ResponseStatus(HttpStatus.OK)
        public EmployeeProfitSummary getEmployeeProfitReport(
                        @ModelAttribute EmployeeProfitFilter filter) {
                EmployeeProfitSummary report = employeeProfitReportPort
                                .generateReport(filter);
                return report;
        }

        @GetMapping("/getEmployeeLifecycleReport")
        @ResponseStatus(HttpStatus.OK)
        public List<EmployeeHistoryResponseDTO> getEmployeeLifecycleReport(
                        @ModelAttribute EmployeeLifecycleFilter filter) {

                List<EmployeeHistoryResponseDTO> report = employeeLifecycleReportPort
                                .generateReport(filter);
                return report;
        }

        @GetMapping("/getDoctorAssignmentReport")
        @ResponseStatus(HttpStatus.OK)
        @Valid
        public List<EmployeeAssignableResponseDTO> getDoctorAssignmentReport(
                        @Valid @ModelAttribute DoctorAssignmentFilter filter) {
                List<EmployeeAssignableResponseDTO> report = doctorAssignmentReportPort
                                .generateReport(filter);
                return report;
        }

}
