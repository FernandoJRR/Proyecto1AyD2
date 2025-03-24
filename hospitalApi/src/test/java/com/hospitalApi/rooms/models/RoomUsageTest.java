package com.hospitalApi.rooms.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hospitalApi.consults.models.Consult;

class RoomUsageTest {

    private static final int USAGE_DAYS = 3;
    private static final BigDecimal USAGE_PRICE = new BigDecimal("450.00");

    private Room room;
    private Consult consult;

    @BeforeEach
    public void setUp() {
        room = new Room();
        consult = new Consult();
    }

    /**
     * dado: datos válidos de uso de habitación.
     * cuando: se crea un objeto RoomUsage con su constructor completo.
     * entonces: los campos se asignan correctamente.
     */
    @Test
    public void shouldCreateRoomUsageWithValidData() {
        // arrange y act
        RoomUsage roomUsage = new RoomUsage(consult, room, USAGE_DAYS, USAGE_PRICE);
        // assertF
        assertAll(
                () -> assertEquals(consult, roomUsage.getConsult()),
                () -> assertEquals(room, roomUsage.getRoom()),
                () -> assertEquals(USAGE_DAYS, roomUsage.getUsageDays()),
                () -> assertEquals(USAGE_PRICE, roomUsage.getPrice()));
    }

    /**
     * dado: una instancia vacía de RoomUsage.
     * cuando: se accede a sus campos sin haber sido inicializados.
     * entonces: todos los campos deben ser nulos o valores por defecto.
     */
    @Test
    public void shouldReturnNullFieldsWhenInstantiatedWithoutArgs() {
        // arrange y assert
        RoomUsage roomUsage = new RoomUsage();
        // assert
        assertAll(
                () -> assertNull(roomUsage.getId()),
                () -> assertNull(roomUsage.getConsult()),
                () -> assertNull(roomUsage.getRoom()),
                () -> assertEquals(0, roomUsage.getUsageDays()),
                () -> assertNull(roomUsage.getPrice()));
    }

    /**
     * dado: una instancia de RoomUsage vacía.
     * cuando: se asignan valores con los setters.
     * entonces: los valores se reflejan correctamente con los getters.
     */
    @Test
    public void shouldUpdateRoomUsageFieldsWithSetters() {
        // arrange
        RoomUsage roomUsage = new RoomUsage();
        // act
        roomUsage.setConsult(consult);
        roomUsage.setRoom(room);
        roomUsage.setUsageDays(USAGE_DAYS);
        roomUsage.setPrice(USAGE_PRICE);
        // assert
        assertAll(
                () -> assertEquals(consult, roomUsage.getConsult()),
                () -> assertEquals(room, roomUsage.getRoom()),
                () -> assertEquals(USAGE_DAYS, roomUsage.getUsageDays()),
                () -> assertEquals(USAGE_PRICE, roomUsage.getPrice()));
    }
}
