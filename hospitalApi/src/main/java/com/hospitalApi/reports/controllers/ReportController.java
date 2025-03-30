package com.hospitalApi.reports.controllers;

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
import com.hospitalApi.reports.ports.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService<List<Medicine>, String> medicineReport;

    private final MedicineMapper medicineMapper;

    @GetMapping("/getMedicinesReport")
    @ResponseStatus(HttpStatus.OK)
    public List<MedicineResponseDTO> getMedicinesReport(
            @RequestParam(required = false) String name) {
        List<Medicine> medicines = medicineReport.generateReport(name);
        List<MedicineResponseDTO> medicinesDTO = medicineMapper.fromMedicineListToMedicineResponseDTOList(medicines);
        return medicinesDTO;
    }
}
