package com.hospitalApi.surgery.models;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.hospitalApi.surgery.dtos.CreateSurgeryTypeRequest;
import com.hospitalApi.surgery.dtos.UpdateSurgeryTypeRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SurgeryTypeTest {

    private static final String TYPE = "Cirugía Mayor";
    private static final String DESCRIPTION = "Procedimientos invasivos complejos";
    private static final BigDecimal SPECIALIST_PAYMENT = new BigDecimal(1500);
    private static final BigDecimal HOSPITAL_COST = new BigDecimal(3000);
    private static final BigDecimal SURGERY_COST = new BigDecimal(4500);

    private static final String UPDATED_TYPE = "Cirugía Menor";
    private static final String UPDATED_DESCRIPTION = "Procedimientos simples";
    private static final BigDecimal UPDATED_SPECIALIST_PAYMENT = new BigDecimal(500);
    private static final BigDecimal UPDATED_HOSPITAL_COST = new BigDecimal(1000);
    private static final BigDecimal UPDATED_SURGERY_COST = new BigDecimal(2000);

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
        CreateSurgeryTypeRequest request = new CreateSurgeryTypeRequest(TYPE, DESCRIPTION, SPECIALIST_PAYMENT,
                HOSPITAL_COST, SURGERY_COST);

        SurgeryType result = new SurgeryType(request);

        assertAll(
                () -> assertEquals(TYPE, result.getType()),
                () -> assertEquals(DESCRIPTION, result.getDescription()),
                () -> assertEquals(SPECIALIST_PAYMENT, result.getSpecialistPayment()),
                () -> assertEquals(HOSPITAL_COST, result.getHospitalCost()),
                () -> assertEquals(SURGERY_COST, result.getSurgeryCost()));
    }

    /**
     * dado: un DTO con campos actualizados.
     * cuando: se actualiza un SurgeryType con updateFromDTO.
     * entonces: los campos se modifican correctamente.
     */
    @Test
    void shouldUpdateSurgeryTypeFromDTO() {
        // arrange
        UpdateSurgeryTypeRequestDTO updateDTO = new UpdateSurgeryTypeRequestDTO();
        updateDTO.setType(UPDATED_TYPE);
        updateDTO.setDescription(UPDATED_DESCRIPTION);
        updateDTO.setSpecialistPayment(UPDATED_SPECIALIST_PAYMENT);
        updateDTO.setHospitalCost(UPDATED_HOSPITAL_COST);
        updateDTO.setSurgeryCost(UPDATED_SURGERY_COST);
        // axct
        surgeryType.updateFromDTO(updateDTO);
        // assert
        assertAll(
                () -> assertEquals(UPDATED_TYPE, surgeryType.getType()),
                () -> assertEquals(UPDATED_DESCRIPTION, surgeryType.getDescription()),
                () -> assertEquals(UPDATED_SPECIALIST_PAYMENT, surgeryType.getSpecialistPayment()),
                () -> assertEquals(UPDATED_HOSPITAL_COST, surgeryType.getHospitalCost()),
                () -> assertEquals(UPDATED_SURGERY_COST, surgeryType.getSurgeryCost()));
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
                () -> assertEquals(original.getSurgeryCost(), surgeryType.getSurgeryCost()));
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
                () -> assertEquals(SURGERY_COST, surgeryType.getSurgeryCost()));
    }
}
