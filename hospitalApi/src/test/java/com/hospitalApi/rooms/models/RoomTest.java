package com.hospitalApi.rooms.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.hospitalApi.rooms.enums.RoomStatus;

class RoomTest {

    private static final String ROOM_ID = "room-001";
    private static final String ROOM_NUMBER = "101";
    private static final String UPDATED_ROOM_NUMBER = "102";
    private static final BigDecimal DAILY_PRICE = new BigDecimal("120.50");
    private static final BigDecimal UPDATED_DAILY_PRICE = new BigDecimal("85.00");

    private static final BigDecimal MAINTENANCE_COST = new BigDecimal("120.50");
    private static final BigDecimal UPDATED_COST = new BigDecimal("85.00");

    /**
     * dado: parámetros válidos sin ID.
     * cuando: se crea una instancia de Room usando el constructor con number, cost
     * y status.
     * entonces: los campos se asignan correctamente y el ID permanece nulo.
     */
    @Test
    public void shouldCreateRoomWithoutId() {
        Room room = new Room(ROOM_NUMBER, DAILY_PRICE, MAINTENANCE_COST, RoomStatus.AVAILABLE);

        assertAll(
                () -> assertNull(room.getId()),
                () -> assertEquals(ROOM_NUMBER, room.getNumber()),
                () -> assertEquals(MAINTENANCE_COST, room.getDailyMaintenanceCost()),
                () -> assertEquals(RoomStatus.AVAILABLE, room.getStatus()));
    }

    /**
     * dado: un ID válido.
     * cuando: se crea una instancia de Room usando el constructor solo con ID.
     * entonces: el ID se asigna correctamente y los demás campos permanecen nulos.
     */
    @Test
    public void shouldCreateRoomWithIdOnly() {
        Room room = new Room(ROOM_ID);

        assertAll(
                () -> assertEquals(ROOM_ID, room.getId()),
                () -> assertNull(room.getNumber()),
                () -> assertNull(room.getDailyMaintenanceCost()),
                () -> assertNull(room.getStatus()));
    }

    /**
     * dado: una instancia de Room.
     * cuando: se actualizan los campos usando setters.
     * entonces: los valores son actualizados correctamente.
     */
    @Test
    public void shouldUpdateRoomFields() {
        Room room = new Room(ROOM_ID);
        room.setNumber(UPDATED_ROOM_NUMBER);
        room.setDailyMaintenanceCost(UPDATED_COST);
        room.setStatus(RoomStatus.OCCUPIED);
        room.setDailyPrice(UPDATED_DAILY_PRICE);
        room.setRoomHistories(List.of());

        assertAll(
                () -> assertNotNull(room.getRoomHistories()),
                () -> assertEquals(UPDATED_ROOM_NUMBER, room.getNumber()),
                () -> assertEquals(UPDATED_COST, room.getDailyMaintenanceCost()),
                () -> assertEquals(RoomStatus.OCCUPIED, room.getStatus()),
                () -> assertEquals(UPDATED_DAILY_PRICE, room.getDailyPrice()));
    }

    /**
     * dado: una instancia de Room vacia.
     * cuando: se crea el objeto con un constructor vacio.
     * entonces: los atributos se establecen como nulos.
     */
    @Test
    public void shouldReturnNullFieldsWhenInstantiatedWithoutArgs() {
        Room room = new Room();

        assertAll(
                () -> assertNull(room.getId()),
                () -> assertNull(room.getNumber()),
                () -> assertNull(room.getDailyMaintenanceCost()),
                () -> assertNull(room.getStatus()),
                () -> assertNull(room.getRoomHistories()),
                () -> assertNull(room.getDailyPrice())

        );
    }
}
