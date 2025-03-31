package com.hospitalApi.reports.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.medicines.dtos.MedicineResponseDTO;
import com.hospitalApi.medicines.mappers.MedicineMapper;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.reports.dtos.request.EmployeeProfitFilter;
import com.hospitalApi.reports.dtos.request.MedicationProfitFilter;
import com.hospitalApi.reports.dtos.request.MedicationReportFilter;
import com.hospitalApi.reports.dtos.response.employeeSalesReport.EmployeeProfitSummary;
import com.hospitalApi.reports.dtos.response.medicationProfitReport.MedicationProfitSummary;
import com.hospitalApi.reports.ports.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService<List<Medicine>, MedicationReportFilter> medicationReportPort;
    private final ReportService<MedicationProfitSummary, MedicationProfitFilter> medicationProfitReportPort;
    private final ReportService<EmployeeProfitSummary, EmployeeProfitFilter> employeeProfitReportPort;

    private final MedicineMapper medicineMapper;

    @GetMapping("/getMedicationReport")
    @ResponseStatus(HttpStatus.OK)
    public List<MedicineResponseDTO> getMedicinesReport(
            @RequestParam(required = false) String name) {
        List<Medicine> medicines = medicationReportPort.generateReport(new MedicationReportFilter(name));
        List<MedicineResponseDTO> medicinesDTO = medicineMapper.fromMedicineListToMedicineResponseDTOList(medicines);
        return medicinesDTO;
    }

    @GetMapping("/getMedicationProfitReport")
    @ResponseStatus(HttpStatus.OK)
    public MedicationProfitSummary getMedicationProfitReport(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        MedicationProfitSummary report = medicationProfitReportPort
                .generateReport(new MedicationProfitFilter(name, startDate, endDate));
        return report;
    }

    @GetMapping("/getEmployeeProfitReport")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeProfitSummary getEmployeeProfitReport(
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String employeeCui) {
        EmployeeProfitSummary report = employeeProfitReportPort
                .generateReport(new EmployeeProfitFilter(employeeName, employeeCui));
        return report;
    }
}
