import type { Entity } from "../utils/entity";
import type { RoomStatus } from "./room-status";

const CURRENT_ROOMS_URI = "/v1/rooms";

export interface Room extends Entity {
  id: string;
  number: string;
  dailyMaintenanceCost: number;
  dailyPrice: number;
  status: RoomStatus;
}

export interface RoomPayLoad {
  number: string;
  dailyMaintenanceCost: number;
  dailyPrice: number;
}

/**
 * Manda a traer todos las habitaciones disponibles en el sistema.
 * @param params
 * @returns
 */
export async function getAllRooms() {
  return await $api<Room[]>(`${CURRENT_ROOMS_URI}/all`);
}

/**
 * Crea una nueva habitación.
 * @param payload datos de la habitación a crear
 * @returns habitación creada
 */
export async function createRoom(payload: RoomPayLoad) {
  return await $api<Room>(`${CURRENT_ROOMS_URI}`, {
    body: payload,
    method: "POST",
  });
}

/**
 * Actualiza una habitación existente.
 * @param roomId ID de la habitación a actualizar
 * @param payload datos nuevos
 * @returns habitación actualizada
 */
export async function updateRoom(roomId: string, payload: RoomPayLoad) {
  return await $api<Room>(`${CURRENT_ROOMS_URI}/${roomId}`, {
    body: payload,
    method: "PATCH",
  });
}

/**
 * Alterna la disponibilidad de una habitación.
 * @param roomId ID de la habitación a alternar disponibilidad
 * @returns habitación con estado actualizado
 */
export async function toggleRoomAvailability(roomId: string) {
  return await $api<Room>(
    `${CURRENT_ROOMS_URI}/${roomId}/toggle-availability`,
    {
      method: "PATCH",
    }
  );
}

/**
 * Obtiene una habitación por número.
 * @param roomNumber número de la habitación
 * @returns habitación encontrada
 */
export async function getRoomByNumber(roomNumber: string) {
  return await $api<Room>(`${CURRENT_ROOMS_URI}/${roomNumber}/by-number`);
}

/**
 * Obtiene una habitación por ID.
 * @param roomId ID de la habitación
 * @returns habitación encontrada
 */
export async function getRoomById(roomId: string) {
  return await $api<Room>(`${CURRENT_ROOMS_URI}/${roomId}`);
}
