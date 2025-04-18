package com.hospitalApi.rooms.ports;

import java.util.List;

import com.hospitalApi.rooms.models.Room;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForRoomPort {

    /**
     * Crea una nueva habitación si no existe otra con el mismo número.
     *
     * @param room la habitación a crear
     * @return la habitación creada
     * @throws IllegalStateException si ya existe una habitación con el mismo número
     */
    Room createRoom(Room room) throws IllegalStateException;

    /**
     * Edita los datos de una habitación existente.
     *
     * @param idUpdatedRoom el ID de la habitación a actualizar
     * @param room          los nuevos datos de la habitación
     * @return la habitación actualizada
     * @throws DuplicatedEntryException si ya existe otra habitación con el mismo
     *                                  número
     * @throws NotFoundException        si no se encuentra la habitación con el ID
     *                                  especificado
     */
    Room editRoom(String idUpdatedRoom, Room room) throws DuplicatedEntryException, NotFoundException;

    /**
     * Busca una habitación por su ID.
     *
     * @param id el ID de la habitación
     * @return la habitación encontrada
     * @throws NotFoundException si no se encuentra la habitación
     */
    Room findRoomById(String id) throws NotFoundException;

    /**
     * Obtiene la lista de todas las habitaciones registradas.
     *
     * @return una lista de habitaciones
     */
    List<Room> findAllRooms();

    /**
     * Busca todas las habitaciones disponibles.
     * 
     * @return una lista de habitaciones disponibles
     */
    List<Room> findAllRoomsAvailable();

    /**
     * Cambia el estado de disponibilidad de una habitación entre AVAILABLE y
     * OUT_OF_SERVICE.
     * Si la habitación está ocupada, se lanza una excepción.
     *
     * @param roomId ID de la habitación a modificar.
     * @return La habitación con el nuevo estado actualizado.
     * @throws NotFoundException     si no se encuentra la habitación con el ID
     *                               proporcionado.
     * @throws IllegalStateException si la habitación está ocupada y no se puede
     *                               cambiar su estado.
     */
    public Room toggleRoomAvailability(String roomId) throws NotFoundException;

    /**
     * Busca una habitación por su number.
     *
     * @param number el number de la habitación
     * @return la habitación encontrada
     * @throws NotFoundException si no se encuentra la habitación
     */
    public Room findRoomByNumber(String number) throws NotFoundException;

    /**
     * Verifica si una habitación está disponible.
     * 
     * @param roomId ID de la habitación a verificar.
     * @return true si la habitación está disponible, false en caso contrario.
     * @throws NotFoundException si no se encuentra la habitación con el ID
     *                           proporcionado.
     */
    public boolean roomIsAvailable(String roomId) throws NotFoundException;

    /**
     * Marca una habitación como vacía.
     * 
     * @param roomId ID de la habitación a marcar como vacía.
     * @return La habitación marcada como vacía.
     * @throws NotFoundException     si no se encuentra la habitación con el ID
     *                               proporcionado.
     * @throws IllegalStateException si la habitación no está ocupada y no se puede
     *                               marcar como vacía.
     */
    public Room markVacant(String roomId) throws NotFoundException, IllegalStateException;

    /**
     * Marca una habitación como disponible.
     * 
     * @param roomId ID de la habitación a marcar como disponible.
     * @return La habitación marcada como disponible.
     * @throws NotFoundException     si no se encuentra la habitación con el ID
     *                               proporcionado.
     * @throws IllegalStateException si la habitación está ocupada y no se puede
     *                               marcar como disponible.
     */
    public Room markAvailable(String roomId) throws NotFoundException, IllegalStateException;

    /**
     * Marca una habitación como ocupada.
     * 
     * @param roomId ID de la habitación a marcar como ocupada.
     * @return La habitación marcada como ocupada.
     * @throws NotFoundException     si no se encuentra la habitación con el ID
     *                               proporcionado.
     * @throws IllegalStateException si la habitación está fuera de servicio y no se
     *                               puede marcar como ocupada.
     */
    public Room markOccupied(String roomId) throws NotFoundException, IllegalStateException;

    /**
     * Marca una habitación como fuera de servicio.
     * 
     * @param roomId ID de la habitación a marcar como fuera de servicio.
     * @return La habitación marcada como fuera de servicio.
     * @throws NotFoundException     si no se encuentra la habitación con el ID
     *                               proporcionado.
     * @throws IllegalStateException si la habitación está ocupada y no se puede
     *                               marcar como fuera de servicio.
     */
    public Room markOutOfService(String roomId) throws NotFoundException, IllegalStateException;
}
