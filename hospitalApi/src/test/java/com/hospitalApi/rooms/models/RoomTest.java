package com.hospitalApi.rooms.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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

    private Room room;

    @BeforeEach
    public void setUp() {
        // vamos a crear una room que eeste toalmente inicializada
        room = new Room(ROOM_NUMBER, DAILY_PRICE, MAINTENANCE_COST, RoomStatus.AVAILABLE);
    }

    /**
     * dado: parámetros válidos sin ID.
     * cuando: se crea una instancia de Room usando el constructor con number, cost
     * y status.
     * entonces: los campos se asignan correctamente y el ID permanece nulo.
     */
    @Test
    public void shouldCreateRoomWithoutId() {
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
        room = new Room(ROOM_ID);

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
        room = new Room(ROOM_ID);
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
        room = new Room();

        assertAll(
                () -> assertNull(room.getId()),
                () -> assertNull(room.getNumber()),
                () -> assertNull(room.getDailyMaintenanceCost()),
                () -> assertNull(room.getStatus()),
                () -> assertNull(room.getRoomHistories()),
                () -> assertNull(room.getDailyPrice())

        );
    }

    /**
     * dado: que la habitación está en estado AVAILABLE o OUT_OF_SERVICE.
     * cuando: se llama al método toggleAvailability().
     * entonces: el estado cambia a OUT_OF_SERVICE o AVAILABLE respectivamenteF.
     */
    @Test
    void shouldToggleBetweenAvailableAndOutOfService() {

        // act y arrange
        room.toggleAvailability();

        // cambiamos una vez el toggle por lo tanto segun el mock general se deberia
        // cmabiar a fuera de servicio
        RoomStatus firstToggle = room.getStatus();

        // act
        // lo volvemos a cambiar y por lo tanto deberia ser habilitada
        room.toggleAvailability();
        RoomStatus secondToggle = room.getStatus();

        // assert
        assertAll(
                () -> assertEquals(RoomStatus.OUT_OF_SERVICE, firstToggle),
                () -> assertEquals(RoomStatus.AVAILABLE, secondToggle));
    }

    /**
     * dado: que la habitación está en estado OCCUPIED.
     * cuando: se llama al método toggleAvailability().
     * entonces: se lanza una excepción `IllegalStateException` proque que no se
     * puede cambiar el estado.
     */
    @Test
    void shouldThrowExceptionWhenRoomIsOccupied() {
        // arragne, por defecto el room estara cupado
        room.setStatus(RoomStatus.OCCUPIED);

        // asseert y act, al detectar que est aocupada entonces lanzara ex epcion
        assertThrows(
                IllegalStateException.class,
                () -> room.toggleAvailability());

    }
}
