package com.hospitalApi.rooms.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hospitalApi.patients.models.Patient;

class RoomHistoryTest {

    private static final String HISTORY_ID = "dsfg-asd-asd";
    private static final LocalDate DATE = LocalDate.of(2024, 5, 15);

    private Room room;
    private Patient patient;

    @BeforeEach
    public void setUp() {
        room = new Room();
        patient = new Patient();
    }

    /**
     * dado: una fecha, habitación y paciente válidos.
     * cuando: se crea un RoomHistory utilizando el constructor con parámetros.
     * entonces: todos los campos se asignan correctamente.
     */
    @Test
    public void shouldCreateRoomHistoryWithValidData() {
        // arrange y act
        RoomHistory roomHistory = new RoomHistory(DATE, room, patient);
        // asset
        assertAll(
                () -> assertEquals(DATE, roomHistory.getDate()),
                () -> assertEquals(room, roomHistory.getRoom()),
                () -> assertEquals(patient, roomHistory.getPatient()));
    }

    /**
     * dado: un id.
     * cuando: se crea un RoomHistory utilizando el constructor con solo el id.
     * entonces: solo el id es asignado, los demas serean nulos.
     */
    @Test
    public void shouldCreateRoomHistoryWithIdOnly() {
        // arrange y act
        RoomHistory roomHistory = new RoomHistory(HISTORY_ID);
        // assert
        assertAll(
                () -> assertNotNull(roomHistory.getId()),
                () -> assertNull(roomHistory.getDate()),
                () -> assertNull(roomHistory.getRoom()),
                () -> assertNull(roomHistory.getPatient()));
    }

    /**
     * dado: que no se pasan argumentos.
     * cuando: se instancia un RoomHistory con el constructor por defecto.
     * entonces: todos los campos deben ser nulos.
     */
    @Test
    public void shouldReturnNullFieldsWhenInstantiatedWithoutArgs() {
        // arrange y act
        RoomHistory roomHistory = new RoomHistory();
        // assert
        assertAll(
                () -> assertNull(roomHistory.getDate()),
                () -> assertNull(roomHistory.getRoom()),
                () -> assertNull(roomHistory.getPatient()),
                () -> assertNull(roomHistory.getId()));
    }

}
