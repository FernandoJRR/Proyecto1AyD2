package com.hospitalApi.rooms.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hospitalApi.consults.models.Consult;

class RoomUsageTest {

    private static final int USAGE_DAYS = 3;
    private static final BigDecimal USAGE_PRICE = new BigDecimal(450);
    private static final BigDecimal DAILY_COST = new BigDecimal(250);
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
        RoomUsage roomUsage = new RoomUsage(consult, room, USAGE_DAYS, USAGE_PRICE, DAILY_COST);
        // assertF
        assertAll(
                () -> assertEquals(consult, roomUsage.getConsult()),
                () -> assertEquals(room, roomUsage.getRoom()),
                () -> assertEquals(USAGE_DAYS, roomUsage.getUsageDays()),
                () -> assertEquals(USAGE_PRICE, roomUsage.getDailyRoomPrice()),
                () -> assertEquals(DAILY_COST, roomUsage.getDailyRoomMaintenanceCost()));
    }

}
