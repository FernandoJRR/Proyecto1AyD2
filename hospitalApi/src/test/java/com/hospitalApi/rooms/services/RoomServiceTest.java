package com.hospitalApi.rooms.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

    /**
     * dado: que la habitación existe y su estado es AVAILABLE.
     * cuando: se llama al método toggleRoomAvailability().
     * entonces: el estado cambia a OUT_OF_SERVICE y se guarda la habitación
     * actualizada.
     */
    @Test
    public void shouldToggleRoomAvailabilityFromAvailableToOutOfService() throws NotFoundException {
        // arrange
        // le damos el estado available al room
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.findById(anyString())).thenReturn(Optional.of(room));
        when(roomRepository.save(any())).thenReturn(room);

        // act
        Room result = roomService.toggleRoomAvailability(ROOM_ID);

        // assert ahora el estado ya n debe ser avilable
        assertAll(
                () -> assertNotNull(result),
                () -> assertNotEquals(RoomStatus.AVAILABLE, result.getStatus()));

        verify(roomRepository, times(1)).save(room);
    }

    /**
     * dado: que existe una habitación con el número solicitado.
     * cuando: se llama al método findRoomByNumber().
     * entonces: se retorna la habitación encontrada.
     */
    @Test
    public void shouldReturnRoomWhenNumberExists() throws NotFoundException {

        when(roomRepository.findByNumber(anyString())).thenReturn(Optional.of(room));

        Room result = roomService.findRoomByNumber(ROOM_NUMBER);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(ROOM_NUMBER, result.getNumber()));

        verify(roomRepository, times(1)).findByNumber(ROOM_NUMBER);
    }

    /**
     * dado: que no existe una habitación con el número solicitado.
     * cuando: se llama al método findRoomByNumber().
     * entonces: se lanza una excepción NotFoundException con el mensaje esperado.
     */
    @Test
    public void shouldThrowNotFoundExceptionWhenRoomNumberDoesNotExist() throws NotFoundException {
        when(roomRepository.findByNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> roomService.findRoomByNumber(ROOM_NUMBER));
    }

    /**
     * dado: que existen habitaciones disponibles con estado AVAILABLE.
     * cuando: se llama al método findAllRoomsAvailable().
     * entonces: se retorna una lista con todas las habitaciones disponibles.
     */
    @Test
    public void findAllRoomsAvailableShouldReturnAvailableRooms() {
        // arrange
        room.setStatus(RoomStatus.AVAILABLE);
        updatedRoom.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.findByStatus(RoomStatus.AVAILABLE)).thenReturn(List.of(room, updatedRoom));

        // act
        List<Room> result = roomService.findAllRoomsAvailable();

        // assert
        verify(roomRepository, times(1)).findByStatus(RoomStatus.AVAILABLE);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(2, result.size()),
                () -> assertTrue(result.contains(room)),
                () -> assertTrue(result.contains(updatedRoom)),
                () -> assertEquals(RoomStatus.AVAILABLE, result.get(0).getStatus()));
    }

    /**
     * dado: que no existen habitaciones disponibles.
     * cuando: se llama al método findAllRoomsAvailable().
     * entonces: se retorna una lista vacía.
     */
    @Test
    public void findAllRoomsAvailableShouldReturnEmptyListWhenNoRoomsAvailable() {
        // arrange
        when(roomRepository.findByStatus(RoomStatus.AVAILABLE)).thenReturn(List.of());

        // act
        List<Room> result = roomService.findAllRoomsAvailable();

        // assert
        verify(roomRepository, times(1)).findByStatus(RoomStatus.AVAILABLE);
        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isEmpty()));
    }

    /**
     * dado: que la habitación existe y su estado es AVAILABLE.
     * cuando: se llama al método roomIsAvailable().
     * entonces: se retorna true.
     */
    @Test
    public void roomIsAvailableShouldReturnTrueWhenRoomIsAvailable() throws NotFoundException {
        // arrange
        room.setId(ROOM_ID);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));

        // act
        boolean result = roomService.roomIsAvailable(ROOM_ID);

        // assert
        assertTrue(result);
        verify(roomRepository, times(1)).findById(ROOM_ID);
    }

    /**
     * dado: que la habitación existe pero su estado no es AVAILABLE.
     * cuando: se llama al método roomIsAvailable().
     * entonces: se retorna false.
     */
    @Test
    public void roomIsAvailableShouldReturnFalseWhenRoomIsNotAvailable() throws NotFoundException {
        // arrange
        room.setId(ROOM_ID);
        room.setStatus(RoomStatus.OCCUPIED);

        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));

        // act
        boolean result = roomService.roomIsAvailable(ROOM_ID);

        // assert
        assertEquals(false, result);
        verify(roomRepository, times(1)).findById(ROOM_ID);
    }

    /**
     * dado: que la habitación no existe.
     * cuando: se llama al método roomIsAvailable().
     * entonces: se lanza una excepción NotFoundException.
     */
    @Test
    public void roomIsAvailableShouldThrowNotFoundExceptionWhenRoomDoesNotExist() {
        // arrange
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class, () -> roomService.roomIsAvailable(ROOM_ID));
        verify(roomRepository, times(1)).findById(ROOM_ID);
    }

    /**
     * dado: que la habitación existe y está en estado OCCUPIED.
     * cuando: se llama al método markVacant().
     * entonces: se cambia su estado a AVAILABLE y se guarda.
     */
    @Test
    public void shouldMarkRoomAsVacantSuccessfullyWhenOccupied() throws NotFoundException {
        // arrange
        room.setId(ROOM_ID);
        room.setStatus(RoomStatus.OCCUPIED);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);

        // act
        Room result = roomService.markVacant(ROOM_ID);

        // assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(RoomStatus.AVAILABLE, result.getStatus()));

        verify(roomRepository, times(1)).findById(ROOM_ID);
        verify(roomRepository, times(1)).save(room);
    }

    /**
     * dado: que la habitación existe pero no está en estado OCCUPIED.
     * cuando: se llama al método markVacant().
     * entonces: se lanza una excepción IllegalStateException.
     */
    @Test
    public void shouldThrowExceptionWhenMarkVacantOnNonOccupiedRoom() {
        // arrange
        room.setId(ROOM_ID);
        room.setStatus(RoomStatus.OUT_OF_SERVICE);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));

        // act & assert
        assertThrows(IllegalStateException.class, () -> roomService.markVacant(ROOM_ID));

        verify(roomRepository, times(1)).findById(ROOM_ID);
        verify(roomRepository, never()).save(any());
    }

    /**
     * dado: que la habitación no existe.
     * cuando: se llama al método markVacant().
     * entonces: se lanza una excepción NotFoundException.
     */
    @Test
    public void shouldThrowNotFoundExceptionWhenRoomDoesNotExistForMarkVacant() {
        // arrange
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class, () -> roomService.markVacant(ROOM_ID));

        verify(roomRepository, times(1)).findById(ROOM_ID);
        verify(roomRepository, never()).save(any());
    }

    /**
     * dado: que la habitación existe y no está en estado OCCUPIED.
     * cuando: se llama al método markAvailable().
     * entonces: se cambia su estado a AVAILABLE y se guarda correctamente.
     */
    @Test
    public void shouldMarkRoomAsAvailableSuccessfully() throws NotFoundException {
        // arrange
        room.setId(ROOM_ID);
        room.setStatus(RoomStatus.OUT_OF_SERVICE); // estado válido para cambiar a AVAILABLE
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);

        // act
        Room result = roomService.markAvailable(ROOM_ID);

        // assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(RoomStatus.AVAILABLE, result.getStatus()));

        verify(roomRepository, times(1)).findById(ROOM_ID);
        verify(roomRepository, times(1)).save(room);
    }

    /**
     * dado: que la habitación existe y está en estado OCCUPIED.
     * cuando: se llama al método markAvailable().
     * entonces: se lanza una excepción IllegalStateException.
     */
    @Test
    public void shouldThrowIllegalStateExceptionWhenMarkAvailableAndRoomIsOccupied() {
        // arrange
        room.setId(ROOM_ID);
        room.setStatus(RoomStatus.OCCUPIED);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));

        // act & assert
        assertThrows(IllegalStateException.class, () -> roomService.markAvailable(ROOM_ID));

        verify(roomRepository, times(1)).findById(ROOM_ID);
        verify(roomRepository, never()).save(any());
    }

    /**
     * dado: que la habitación no existe.
     * cuando: se llama al método markAvailable().
     * entonces: se lanza una excepción NotFoundException.
     */
    @Test
    public void shouldThrowNotFoundExceptionWhenRoomNotFoundInMarkAvailable() {
        // arrange
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class, () -> roomService.markAvailable(ROOM_ID));

        verify(roomRepository, times(1)).findById(ROOM_ID);
        verify(roomRepository, never()).save(any());
    }

    /**
     * dado: que la habitación existe y no está fuera de servicio.
     * cuando: se llama al método markOccupied().
     * entonces: se cambia su estado a OCCUPIED y se guarda correctamente.
     */
    @Test
    public void shouldMarkRoomAsOccupiedSuccessfully() throws NotFoundException {
        // arrange
        room.setId(ROOM_ID);
        room.setStatus(RoomStatus.AVAILABLE); // estado válido para pasar a OCCUPIED
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);

        // act
        Room result = roomService.markOccupied(ROOM_ID);

        // assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(RoomStatus.OCCUPIED, result.getStatus()));

        verify(roomRepository, times(1)).findById(ROOM_ID);
        verify(roomRepository, times(1)).save(room);
    }

    /**
     * dado: que la habitación existe pero está en estado OUT_OF_SERVICE.
     * cuando: se llama al método markOccupied().
     * entonces: se lanza una excepción IllegalStateException.
     */
    @Test
    public void shouldThrowIllegalStateExceptionWhenMarkOccupiedAndRoomIsOutOfService() {
        // arrange
        room.setId(ROOM_ID);
        room.setStatus(RoomStatus.OUT_OF_SERVICE);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));

        // act & assert
        assertThrows(IllegalStateException.class, () -> roomService.markOccupied(ROOM_ID));

        verify(roomRepository, times(1)).findById(ROOM_ID);
        verify(roomRepository, never()).save(any());
    }

    /**
     * dado: que la habitación no existe en la base de datos.
     * cuando: se llama al método markOccupied().
     * entonces: se lanza una excepción NotFoundException.
     */
    @Test
    public void shouldThrowNotFoundExceptionWhenRoomNotFoundInMarkOccupied() {
        // arrange
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class, () -> roomService.markOccupied(ROOM_ID));

        verify(roomRepository, times(1)).findById(ROOM_ID);
        verify(roomRepository, never()).save(any());
    }

    /**
     * dado: que la habitación existe y no está ocupada.
     * cuando: se llama al método markOutOfService().
     * entonces: se cambia su estado a OUT_OF_SERVICE y se guarda correctamente.
     */
    @Test
    public void shouldMarkRoomAsOutOfServiceSuccessfully() throws NotFoundException {
        // arrange
        room.setId(ROOM_ID);
        room.setStatus(RoomStatus.AVAILABLE); // estado válido para cambiar a OUT_OF_SERVICE
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);

        // act
        Room result = roomService.markOutOfService(ROOM_ID);

        // assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(RoomStatus.OUT_OF_SERVICE, result.getStatus()));

        verify(roomRepository, times(1)).findById(ROOM_ID);
        verify(roomRepository, times(1)).save(room);
    }

    /**
     * dado: que la habitación está en estado OCCUPIED.
     * cuando: se llama al método markOutOfService().
     * entonces: se lanza una excepción IllegalStateException.
     */
    @Test
    public void shouldThrowIllegalStateExceptionWhenRoomIsOccupiedInMarkOutOfService() {
        // arrange
        room.setId(ROOM_ID);
        room.setStatus(RoomStatus.OCCUPIED); // estado inválido
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));

        // act & assert
        assertThrows(IllegalStateException.class, () -> roomService.markOutOfService(ROOM_ID));

        verify(roomRepository, times(1)).findById(ROOM_ID);
        verify(roomRepository, never()).save(any());
    }

    /**
     * dado: que la habitación no existe.
     * cuando: se llama al método markOutOfService().
     * entonces: se lanza una excepción NotFoundException.
     */
    @Test
    public void shouldThrowNotFoundExceptionWhenRoomNotFoundInMarkOutOfService() {
        // arrange
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class, () -> roomService.markOutOfService(ROOM_ID));

        verify(roomRepository, times(1)).findById(ROOM_ID);
        verify(roomRepository, never()).save(any());
    }

}
