package com.hospitalApi.reports.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.medicines.dtos.MedicineResponseDTO;
import com.hospitalApi.medicines.mappers.MedicineMapper;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.ports.ForMedicinePort;
import com.hospitalApi.reports.dtos.request.MedicationReportFilter;

@ExtendWith(MockitoExtension.class)
public class MedicationReportServiceTest {

    @Mock
    private MedicineMapper medicineMapper;

    @Mock
    private ForMedicinePort forMedicinePort;

    @InjectMocks
    private MedicationReportService medicationReportService;

    private static final String MEDICINE_NAME = "ibuprofeno";
    private static final String MEDICINE_NAME_2 = "ibuprofeno23";

    private Medicine medicine1;
    private Medicine medicine2;

    private MedicineResponseDTO medicineResponseDTO1;
    private MedicineResponseDTO medicineResponseDTO2;
    private MedicationReportFilter medicationFilter;

    @BeforeEach
    void setUp() {
        medicine1 = new Medicine();
        medicine1.setName(MEDICINE_NAME);
        medicine2 = new Medicine();
        medicine2.setName(MEDICINE_NAME_2);
        medicationFilter = new MedicationReportFilter(MEDICINE_NAME);

        medicineResponseDTO1 = new MedicineResponseDTO(null, MEDICINE_NAME, null, null, null, null, null);
        medicineResponseDTO2 = new MedicineResponseDTO(null, MEDICINE_NAME_2, null, null, null, null, null);
    }

    /**
     * dado: que existe una lista de medicamentos con un nombre específico.
     * cuando: se genera un reporte con dicho nombre.
     * entonces: se devuelve la lista correspondiente desde el puerto.
     */
    @Test
    void testGenerateReport() {
        List<Medicine> mockMedicines = List.of(medicine1, medicine2);
        List<MedicineResponseDTO> expectedList = List.of(medicineResponseDTO1, medicineResponseDTO2);
        // Arrange
        when(forMedicinePort.getAllMedicines(anyString())).thenReturn(mockMedicines);
        when(medicineMapper.fromMedicineListToMedicineResponseDTOList(anyList())).thenReturn(
                expectedList);

        // Act
        List<MedicineResponseDTO> result = medicationReportService.generateReport(medicationFilter);

        // Assert
        assertAll(
                () -> assertEquals(result.size(), 2),
                () -> assertEquals(result.get(0).getName(), MEDICINE_NAME),
                () -> assertEquals(result.get(1).getName(), MEDICINE_NAME_2));

        verify(forMedicinePort, times(1)).getAllMedicines(MEDICINE_NAME);
    }
}
