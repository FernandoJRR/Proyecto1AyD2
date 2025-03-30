package com.hospitalApi.surgery.models;

import static org.junit.jupiter.api.Assertions.*;

import com.hospitalApi.surgery.dtos.CreateSurgeryTypeRequest;
import com.hospitalApi.surgery.dtos.UpdateSurgeryTypeRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SurgeryTypeTest {

    private static final String TYPE = "Cirugía Mayor";
    private static final String DESCRIPTION = "Procedimientos invasivos complejos";
    private static final Double SPECIALIST_PAYMENT = 1500.0;
    private static final Double HOSPITAL_COST = 3000.0;
    private static final Double SURGERY_COST = 4500.0;

    private SurgeryType surgeryType;

    @BeforeEach
    void setUp() {
        surgeryType = new SurgeryType(TYPE, DESCRIPTION, SPECIALIST_PAYMENT, HOSPITAL_COST, SURGERY_COST);
    }

    /**
     * dado: un objeto CreateSurgeryTypeRequest válido.
     * cuando: se crea una instancia de SurgeryType.
     * entonces: los campos se inicializan correctamente.
     */
    @Test
    void shouldCreateSurgeryTypeFromCreateRequest() {
        CreateSurgeryTypeRequest request = new CreateSurgeryTypeRequest(TYPE, DESCRIPTION, SPECIALIST_PAYMENT, HOSPITAL_COST, SURGERY_COST);

        SurgeryType result = new SurgeryType(request);

        assertAll(
                () -> assertEquals(TYPE, result.getType()),
                () -> assertEquals(DESCRIPTION, result.getDescription()),
                () -> assertEquals(SPECIALIST_PAYMENT, result.getSpecialistPayment()),
                () -> assertEquals(HOSPITAL_COST, result.getHospitalCost()),
                () -> assertEquals(SURGERY_COST, result.getSurgeryCost())
        );
    }

    /**
     * dado: un DTO con campos actualizados.
     * cuando: se actualiza un SurgeryType con updateFromDTO.
     * entonces: los campos se modifican correctamente.
     */
    @Test
    void shouldUpdateSurgeryTypeFromDTO() {
        UpdateSurgeryTypeRequestDTO updateDTO = new UpdateSurgeryTypeRequestDTO();
        updateDTO.setType("Cirugía Menor");
        updateDTO.setDescription("Procedimientos simples");
        updateDTO.setSpecialistPayment(500.0);
        updateDTO.setHospitalCost(1000.0);
        updateDTO.setSurgeryCost(2000.0);

        surgeryType.updateFromDTO(updateDTO);

        assertAll(
                () -> assertEquals("Cirugía Menor", surgeryType.getType()),
                () -> assertEquals("Procedimientos simples", surgeryType.getDescription()),
                () -> assertEquals(500.0, surgeryType.getSpecialistPayment()),
                () -> assertEquals(1000.0, surgeryType.getHospitalCost()),
                () -> assertEquals(2000.0, surgeryType.getSurgeryCost())
        );
    }

    /**
     * dado: un DTO nulo.
     * cuando: se llama a updateFromDTO.
     * entonces: no se realizan cambios en la entidad.
     */
    @Test
    void shouldNotUpdateWhenDTOIsNull() {
        SurgeryType original = new SurgeryType(TYPE, DESCRIPTION, SPECIALIST_PAYMENT, HOSPITAL_COST, SURGERY_COST);
        surgeryType.updateFromDTO(null);

        assertAll(
                () -> assertEquals(original.getType(), surgeryType.getType()),
                () -> assertEquals(original.getDescription(), surgeryType.getDescription()),
                () -> assertEquals(original.getSpecialistPayment(), surgeryType.getSpecialistPayment()),
                () -> assertEquals(original.getHospitalCost(), surgeryType.getHospitalCost()),
                () -> assertEquals(original.getSurgeryCost(), surgeryType.getSurgeryCost())
        );
    }

    /**
     * dado: un DTO con campos nulos.
     * cuando: se llama a updateFromDTO.
     * entonces: no se actualizan los campos existentes.
     */
    @Test
    void shouldNotUpdateFieldsWhenDTOFieldsAreNull() {
        UpdateSurgeryTypeRequestDTO updateDTO = new UpdateSurgeryTypeRequestDTO();

        surgeryType.updateFromDTO(updateDTO);

        assertAll(
                () -> assertEquals(TYPE, surgeryType.getType()),
                () -> assertEquals(DESCRIPTION, surgeryType.getDescription()),
                () -> assertEquals(SPECIALIST_PAYMENT, surgeryType.getSpecialistPayment()),
                () -> assertEquals(HOSPITAL_COST, surgeryType.getHospitalCost()),
                () -> assertEquals(SURGERY_COST, surgeryType.getSurgeryCost())
        );
    }
}
