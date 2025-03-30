package com.hospitalApi.reports.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.ports.ForMedicinePort;
import com.hospitalApi.reports.ports.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicationReportService implements ReportService<List<Medicine>, String> {

    private final ForMedicinePort forMedicinePort;

    /**
     * Genera un reporte de medicamentos en base al nombre del medicamento
     * 
     * @param name nombre del medicamento
     * @return lista de medicamentos
     */
    @Override
    public List<Medicine> generateReport(String name) {
        // buscamos la medicina por nombre
        List<Medicine> medicines = forMedicinePort.getAllMedicines(name);
        return medicines;
    }

}
