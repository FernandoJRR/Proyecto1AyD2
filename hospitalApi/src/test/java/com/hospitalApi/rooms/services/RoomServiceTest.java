package com.hospitalApi.rooms.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.rooms.enums.RoomStatus;
import com.hospitalApi.rooms.models.Room;
import com.hospitalApi.rooms.repositories.RoomRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private static final String ROOM_ID = "1234-123453";
    private static final String ROOM_NUMBER = "101";
    private static final BigDecimal DAILY_PRICE = new BigDecimal("150.00");
    private static final BigDecimal MAINTENANCE_COST = new BigDecimal("50.00");

    private static final String UPDATED_ROOM_NUMBER = "202";
    private static final BigDecimal UPDATED_DAILY_PRICE = new BigDecimal("180.00");
    private static final BigDecimal UPDATED_MAINTENANCE_COST = new BigDecimal("60.00");

    private Room room;

    private Room updatedRoom;

    @BeforeEach
    private void setUp() {
        room = new Room(ROOM_NUMBER, DAILY_PRICE, MAINTENANCE_COST, null);
        updatedRoom = new Room(UPDATED_ROOM_NUMBER, UPDATED_DAILY_PRICE, UPDATED_MAINTENANCE_COST, null);
    }

    /**
     * dado: que no existe una habitación con el mismo número.
     * cuando: se intenta crear una nueva habitación.
     * entonces: se guarda correctamente la habitación con los valores definidos.
     */
    @Test
    public void createRoomShouldCreateRoomWhenNumberDoesNotExist() {

        // Arrange
        when(roomRepository.existsByNumber(anyString())).thenReturn(false);// decimos que no existe el nombe
        when(roomRepository.save(room)).thenReturn(room);// al guardar devolvemos nuestro mock

        // Act
        Room result = roomService.createRoom(room);
        // tenemos que captrurar el parametro enviado a save para corrobaorar los seters
        ArgumentCaptor<Room> argumentCaptorRomm = ArgumentCaptor.forClass(Room.class);

        // los metodos se debieron ejecutar una vez
        verify(roomRepository, times(1)).existsByNumber(ROOM_NUMBER);
        verify(roomRepository, times(1)).save(argumentCaptorRomm.capture());
        // extraer lo capturado para los assert correpondintes
        Room capturedRoom = argumentCaptorRomm.getValue();
        // Assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(RoomStatus.AVAILABLE, capturedRoom.getStatus()),
                () -> assertEquals(ROOM_NUMBER, result.getNumber()),
                () -> assertEquals(DAILY_PRICE, result.getDailyPrice()),
                () -> assertEquals(MAINTENANCE_COST, result.getDailyMaintenanceCost()));
    }

    /**
     * dado: que ya existe una habitación con el mismo número.
     * cuando: se intenta crear una nueva habitación con ese número.
     * entonces: se lanza una excepción IllegalStateException y no se guarda la
     * entidad.
     */
    @Test
    public void createRoomShouldThrowExceptionWhenRoomNumberAlreadyExists() {
        // Arrange
        // con cualquier string siempre devolver truex
        when(roomRepository.existsByNumber(anyString())).thenReturn(true);

        // Act y Assert
        assertThrows(IllegalStateException.class, () -> {
            roomService.createRoom(room);
        });

        verify(roomRepository, times(1)).existsByNumber(anyString());
        verify(roomRepository, never()).save(any(Room.class));
    }

    /**
     * dado: que existe una habitación con el ID proporcionado.
     * cuando: se actualizan sus datos con un número válido no duplicado.
     * entonces: se guardan los nuevos datos correctamente en la habitación.
     */
    @Test
    public void editRoomShouldUpdateRoomSuccessfully() throws DuplicatedEntryException, NotFoundException {
        // Arrange
        room.setId(ROOM_ID);
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(room));
        when(roomRepository.existsByNumberAndIdIsNot(anyString(), anyString())).thenReturn(false);
        when(roomRepository.save(any())).thenReturn(updatedRoom);

        // Act
        Room result = roomService.editRoom(ROOM_ID, updatedRoom);

        // tenemos que captrurar el parametro enviado a save para corrobaorar los seters
        ArgumentCaptor<Room> argumentCaptorRomm = ArgumentCaptor.forClass(Room.class);
        // los metodos se debieron ejecutar una vez
        verify(roomRepository, times(1)).existsByNumberAndIdIsNot(UPDATED_ROOM_NUMBER, ROOM_ID);
        verify(roomRepository, times(1)).save(argumentCaptorRomm.capture());
        // extraer lo capturado para los assert correpondintes
        Room capturedRoom = argumentCaptorRomm.getValue();

        // Assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(UPDATED_ROOM_NUMBER, capturedRoom.getNumber()),
                () -> assertEquals(UPDATED_DAILY_PRICE, capturedRoom.getDailyPrice()),
                () -> assertEquals(UPDATED_MAINTENANCE_COST, capturedRoom.getDailyMaintenanceCost()));

    }

    /**
     * dado: que ya existe otra habitación con el mismo número.
     * cuando: se intenta actualizar una habitación con ese número.
     * entonces: se lanza una excepción DuplicatedEntryException y no se guarda la
     * habitación.
     */

    @Test
    public void editRoomShouldThrowDuplicatedEntryExceptionWhenNumberExistsInAnotherRoom()
            throws DuplicatedEntryException, NotFoundException {
        // Arrange
        room.setId(ROOM_ID);
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(room));
        when(roomRepository.existsByNumberAndIdIsNot(anyString(), anyString())).thenReturn(true);

        // Act y assert
        assertThrows(DuplicatedEntryException.class, () -> {
            roomService.editRoom(ROOM_ID, updatedRoom);
        });

        // los metodos se debieron ejecutar una vez
        verify(roomRepository, times(1)).existsByNumberAndIdIsNot(UPDATED_ROOM_NUMBER, ROOM_ID);
        verify(roomRepository, never()).save(any());

    }

    /**
     * dado: que no existe una habitación con el ID proporcionado.
     * cuando: se intenta actualizar sus datos.
     * entonces: se lanza una excepción NotFoundException y no se realiza ninguna
     * operación.
     */

    @Test
    public void editRoomShouldThrowNotFoundExceptionWhenRoomDoesNotExist() {
        // Arrange
        when(roomRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(NotFoundException.class, () -> {
            roomService.editRoom(ROOM_ID, updatedRoom);
        });
        verify(roomRepository, never()).save(any());
    }

    /**
     * dado: que existe una habitación con el ID solicitado.
     * cuando: se busca mediante findRoomById.
     * entonces: se retorna correctamente la habitación correspondiente.
     */
    @Test
    public void findRoomByIdShouldReturnRoomWhenIdExists() throws NotFoundException {
        // Arrange
        room.setId(ROOM_ID);
        // retornar un existente
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));

        // Act
        Room result = roomService.findRoomById(ROOM_ID);

        // Assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(ROOM_ID, result.getId()),
                () -> assertEquals(ROOM_NUMBER, result.getNumber()),
                () -> assertEquals(DAILY_PRICE, result.getDailyPrice()),
                () -> assertEquals(MAINTENANCE_COST, result.getDailyMaintenanceCost()));

        verify(roomRepository, times(1)).findById(ROOM_ID);
    }

    /**
     * dado: que no existe ninguna habitación con el ID solicitado.
     * cuando: se invoca findRoomById.
     * entonces: se lanza una excepción NotFoundException.
     */

    @Test
    public void findRoomByIdShouldThrowNotFoundExceptionWhenRoomDoesNotExist() {
        // Arrange
        // al retonrar vacio solito generara una excepcion
        when(roomRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(NotFoundException.class, () -> {
            roomService.findRoomById(ROOM_ID);
        });
        verify(roomRepository, times(1)).findById(ROOM_ID);
    }

    /**
     * dado: que existen habitaciones registradas.
     * cuando: se invoca findAllRooms.
     * entonces: se retorna una lista con todas las habitaciones encontradas.
     */
    @Test
    public void findAllRoomsShouldReturnAllRooms() {
        // Arrange
        // cuando se invoque al repo entones devovlver una lista llena con moks
        when(roomRepository.findAll()).thenReturn(List.of(room, updatedRoom));

        // Act
        List<Room> result = roomService.findAllRooms();

        // verify
        // este metodo se deio ejecutar una sola vez
        verify(roomRepository, times(1)).findAll();
        // asserts
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertTrue(result.contains(room)),
                () -> assertTrue(result.contains(updatedRoom)));

    }

}
