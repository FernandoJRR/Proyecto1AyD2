import type { Entity } from "../utils/entity";

const CURRENT_PERMISSIONS_URI = "/v1/permissions";

export interface Permission extends Entity {
  id: string;
  name: string;
}

/**
 * Manda a traer todos los permisos disponibles en el sistema.
 * @param params 
 * @returns 
 */
export async function getAllPermissions(params?: {}) {
  return await $api<Permission[]>(`${CURRENT_PERMISSIONS_URI}`, {
    params
  })
}