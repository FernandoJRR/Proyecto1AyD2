package com.hospitalApi.rooms.services;

import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.rooms.models.Room;
import com.hospitalApi.rooms.models.RoomHistory;
import com.hospitalApi.rooms.repositories.RoomHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomHistoryServiceTest {

    private static final String PATIENT_ID = "patient-id";
    private static final String PATIENT_FIRSTNAME = "John";
    private static final String PATIENT_LASTNAME = "Doe";
    private static final String ROOM_ID = "room-id";
    private static final String ROOM_NUMBER = "101";
    private static final LocalDate TODAY = LocalDate.now();

    @Mock
    private RoomHistoryRepository roomHistoryRepository;

    @InjectMocks
    private RoomHistoryService roomHistoryService;

    private Patient patient;
    private Room room;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patient = new Patient();
        patient.setId(PATIENT_ID);
        patient.setFirstnames(PATIENT_FIRSTNAME);
        patient.setLastnames(PATIENT_LASTNAME);

        room = new Room();
        room.setId(ROOM_ID);
        room.setNumber(ROOM_NUMBER);
    }

    @Test
    void saveHistory_ShouldCreateAndSaveRoomHistory() {
        // Arrange
        ArgumentCaptor<RoomHistory> captor = ArgumentCaptor.forClass(RoomHistory.class);
        RoomHistory expectedHistory = new RoomHistory(TODAY, room, patient);
        when(roomHistoryRepository.save(any(RoomHistory.class))).thenReturn(expectedHistory);

        // Act
        RoomHistory result = roomHistoryService.saveHistory(patient, room);

        // Assert
        verify(roomHistoryRepository, times(1)).save(captor.capture());
        RoomHistory savedHistory = captor.getValue();

        assertNotNull(result);
        assertEquals(expectedHistory.getPatient(), result.getPatient());
        assertEquals(expectedHistory.getRoom(), result.getRoom());
        assertEquals(TODAY, result.getDate());

        assertEquals(patient, savedHistory.getPatient());
        assertEquals(room, savedHistory.getRoom());
        assertEquals(TODAY, savedHistory.getDate());
    }
}
