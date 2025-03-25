package com.hospitalApi.shared.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

class AuditorTest {

    private static final String AUDITOR_ID_1 = "13245-213435";
    private static final String AUDITOR_ID_2 = "145-2sdf3435";
    private static final LocalDate CREATED_AT = LocalDate.now();
    private static final LocalDate UPDATED_AT = LocalDate.now();

    private Auditor auditor1 = new Auditor(AUDITOR_ID_1);
    private Auditor auditor2 = new Auditor(AUDITOR_ID_2);

    @BeforeEach
    private void setUp() {
        auditor1 = new Auditor(AUDITOR_ID_1);
        auditor2 = new Auditor(AUDITOR_ID_2);
    }

    /**
     * dado: un ID válido.
     * cuando: se crea una instancia de Auditor con constructor por ID.
     * entonces: el ID se asigna correctamente y el resto permanece nulo.
     */
    @Test
    public void shouldCreateAuditorWithIdOnly() {
        // assert
        assertAll(
                () -> assertEquals(AUDITOR_ID_1, auditor1.getId()),
                () -> assertNull(auditor1.getCreatedAt()),
                () -> assertNull(auditor1.getUpdateAt()));
    }

    /**
     * dado: una instancia vacía de Auditor.
     * cuando: se accede a sus campos sin haber sido inicializados.
     * entonces: todos los campos deben ser nulos.
     */
    @Test
    public void shouldReturnNullFieldsWhenCreatedWithNoArgs() {
        auditor1 = new Auditor();
        // assert
        assertAll(
                () -> assertNull(auditor1.getId()),
                () -> assertNull(auditor1.getCreatedAt()),
                () -> assertNull(auditor1.getUpdateAt()));
    }

    /**
     * dado: una instancia de Auditor.
     * cuando: se asignan valores manualmente a los campos.
     * entonces: los getters devuelven los valores esperados.
     */
    @Test
    public void shouldUpdateAuditorFieldsUsingSetters() {
        auditor1 = new Auditor();
        // arrange y act
        auditor1.setId(AUDITOR_ID_1);
        auditor1.setCreatedAt(CREATED_AT);
        auditor1.setUpdateAt(UPDATED_AT);
        // assert
        assertAll(
                () -> assertEquals(AUDITOR_ID_1, auditor1.getId()),
                () -> assertEquals(CREATED_AT, auditor1.getCreatedAt()),
                () -> assertEquals(UPDATED_AT, auditor1.getUpdateAt()));
    }

    /**
     * dado: dos instancias con el mismo ID.
     * cuando: se comparan usando equals y se obtiene su hashCode.
     * entonces: equals devuelve true y hashCode es igual.
     */
    @Test
    public void shouldConsiderAuditorsEqualWhenIdsAreSame() {
        // arrange y acct
        auditor2.setId(AUDITOR_ID_1);
        // assert
        assertAll(
                () -> assertEquals(auditor1, auditor2),
                () -> assertEquals(auditor1.hashCode(), auditor2.hashCode()));
    }

    /**
     * dado: dos instancias con IDs distintos.
     * cuando: se comparan usando equals y se obtiene su hashCode.
     * entonces: equals devuelve false y los hashCodes son distintos.
     */
    @Test
    public void shouldConsiderAuditorsNotEqualWhenIdsAreDifferent() {
        // assert
        assertAll(
                () -> assertNotEquals(auditor1, auditor2),
                () -> assertNotEquals(auditor1.hashCode(), auditor2.hashCode()));
    }

    /**
     * dado: una instancia de Auditor.
     * cuando: se compara consigo misma.
     * entonces: equals devuelve true.
     */
    @Test
    public void shouldBeEqualToItself() {
        assertEquals(auditor1, auditor1);
    }

    /**
     * dado: una instancia de Auditor.
     * cuando: se compara con null o con otro tipo.
     * entonces: equals devuelve false.
     */
    @Test
    public void shouldNotBeEqualToNullOrDifferentType() {
        assertAll(
                () -> assertNotEquals(auditor1, null),
                () -> assertNotEquals(auditor1.toString(), "estoesUnStringCualquieraBrow"));
    }

}
