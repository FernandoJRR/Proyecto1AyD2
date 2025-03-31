package com.hospitalApi.rooms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.rooms.enums.RoomStatus;
import com.hospitalApi.rooms.models.Room;
import com.hospitalApi.rooms.models.RoomUsage;
import com.hospitalApi.rooms.ports.ForRoomHistoryPort;
import com.hospitalApi.rooms.ports.ForRoomPort;
import com.hospitalApi.rooms.ports.ForRoomUsagePort;
import com.hospitalApi.rooms.repositories.RoomUsageRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RoomUsageServiceTest {

    private static final String ROOM_ID = "ROOM-001";
    private static final String CONSULT_ID = "CONSULT-001";
    private static final String PATIENT_ID = "PATIENT-001";
    private static final BigDecimal DAILY_PRICE = new BigDecimal("150.00");

    @Mock
    private ForRoomPort forRoomPort;

    @Mock
    private ForRoomHistoryPort forRoomHistoryPort;

    @Mock
    private ForRoomUsagePort forRoomUsagePort;

    @Mock
    private RoomUsageRepository roomUsageRepository;

    @InjectMocks
    private RoomUsageService roomUsageService;

    private Room room;
    private Consult consult;
    private Patient patient;
    private RoomUsage roomUsage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        patient = new Patient("John", "Doe", "123456789");
        patient.setId(PATIENT_ID);

        consult = new Consult();
        consult.setId(CONSULT_ID);
        consult.setPatient(patient);

        room = new Room();
        room.setId(ROOM_ID);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setDailyPrice(DAILY_PRICE);

        roomUsage = new RoomUsage(consult, room, 1, DAILY_PRICE);
        roomUsage.setCreatedAt(LocalDate.now().minusDays(1));
    }

    @Test
    public void shouldAssignRoomToConsultSuccessfully() throws Exception {
        // arrange
        when(forRoomPort.findRoomById(ROOM_ID)).thenReturn(room);
        when(forRoomPort.roomIsAvailable(ROOM_ID)).thenReturn(true);
        when(roomUsageRepository.existsByConsultId(CONSULT_ID)).thenReturn(false);
        when(roomUsageRepository.existsByRoomIdAndConsultId(ROOM_ID, CONSULT_ID)).thenReturn(false);
        when(roomUsageRepository.save(any(RoomUsage.class))).thenReturn(roomUsage);

        // act
        RoomUsage result = roomUsageService.asignRoomToConsult(ROOM_ID, consult);

        // assert
        assertNotNull(result);
        assertEquals(CONSULT_ID, result.getConsult().getId());
        assertEquals(ROOM_ID, result.getRoom().getId());
        verify(forRoomPort).markOccupied(ROOM_ID);
        verify(forRoomHistoryPort).saveHistory(patient, room);
    }

    @Test
    public void shouldThrowWhenRoomNotAvailable() throws NotFoundException {
        // arrange
        when(forRoomPort.findRoomById(ROOM_ID)).thenReturn(room);
        when(forRoomPort.roomIsAvailable(ROOM_ID)).thenReturn(false);

        // assert & act
        assertThrows(IllegalStateException.class, () -> roomUsageService.asignRoomToConsult(ROOM_ID, consult));
    }

    @Test
    public void shouldThrowWhenConsultAlreadyHasRoom() throws NotFoundException {
        // arrange
        when(forRoomPort.findRoomById(ROOM_ID)).thenReturn(room);
        when(forRoomPort.roomIsAvailable(ROOM_ID)).thenReturn(true);
        when(roomUsageRepository.existsByConsultId(CONSULT_ID)).thenReturn(true);

        // assert & act
        assertThrows(DuplicatedEntryException.class, () -> roomUsageService.asignRoomToConsult(ROOM_ID, consult));
    }

    @Test
    public void shouldThrowWhenRoomAlreadyAssignedToConsult() throws NotFoundException {
        // arrange
        when(forRoomPort.findRoomById(ROOM_ID)).thenReturn(room);
        when(forRoomPort.roomIsAvailable(ROOM_ID)).thenReturn(true);
        when(roomUsageRepository.existsByConsultId(CONSULT_ID)).thenReturn(false);
        when(roomUsageRepository.existsByRoomIdAndConsultId(ROOM_ID, CONSULT_ID)).thenReturn(true);

        // assert & act
        assertThrows(DuplicatedEntryException.class, () -> roomUsageService.asignRoomToConsult(ROOM_ID, consult));
    }

    @Test
    public void shouldCloseRoomUsageSuccessfully() throws Exception {
        // arrange
        when(roomUsageRepository.existsByConsultId(CONSULT_ID)).thenReturn(true);
        when(roomUsageRepository.findByConsultId(CONSULT_ID)).thenReturn(roomUsage);
        when(roomUsageRepository.save(any(RoomUsage.class))).thenReturn(roomUsage);

        // act
        RoomUsage result = roomUsageService.closeRoomUsage(consult);

        // assert
        assertNotNull(result);
        assertTrue(result.getUsageDays() > 0);
        verify(forRoomPort).markVacant(ROOM_ID);
    }

    @Test
    public void shouldThrowWhenClosingUsageWithoutAssignment() {
        // arrange
        when(roomUsageRepository.existsByConsultId(CONSULT_ID)).thenReturn(false);

        // assert & act
        assertThrows(IllegalStateException.class, () -> roomUsageService.closeRoomUsage(consult));
    }

    @Test
    public void shouldCalculateRoomUsageSuccessfully() {
        // arrange
        when(roomUsageRepository.existsByConsultId(CONSULT_ID)).thenReturn(true);
        when(roomUsageRepository.findByConsultId(CONSULT_ID)).thenReturn(roomUsage);
        when(roomUsageRepository.save(any(RoomUsage.class))).thenReturn(roomUsage);

        // act
        RoomUsage result = roomUsageService.calcRoomUsage(consult);

        // assert
        assertNotNull(result);
        assertTrue(result.getUsageDays() > 0);
    }

    @Test
    public void shouldThrowWhenCalculatingUsageWithoutAssignment() {
        // arrange
        when(roomUsageRepository.existsByConsultId(CONSULT_ID)).thenReturn(false);

        // assert & act
        assertThrows(IllegalStateException.class, () -> roomUsageService.calcRoomUsage(consult));
    }
}
