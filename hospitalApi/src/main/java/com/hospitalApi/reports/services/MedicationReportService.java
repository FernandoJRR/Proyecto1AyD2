package com.hospitalApi.reports.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.medicines.dtos.MedicineResponseDTO;
import com.hospitalApi.medicines.mappers.MedicineMapper;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.ports.ForMedicinePort;
import com.hospitalApi.reports.dtos.request.MedicationReportFilter;
import com.hospitalApi.reports.ports.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicationReportService implements ReportService<List<MedicineResponseDTO>, MedicationReportFilter> {

    private final ForMedicinePort forMedicinePort;
    private final MedicineMapper medicineMapper;

    /**
     * Genera un reporte de medicamentos en base al nombre del medicamento
     * 
     * @param name nombre del medicamento
     * @return lista de medicamentos en formato dto
     */
    @Override
    public List<MedicineResponseDTO> generateReport(MedicationReportFilter filter) {
        // buscamos la medicina por nombre
        List<Medicine> medicines = forMedicinePort.getAllMedicines(filter.getMedicationName());
        List<MedicineResponseDTO> medicinesDTO = medicineMapper
                .fromMedicineListToMedicineResponseDTOList(medicines);
        return medicinesDTO;
    }

}
