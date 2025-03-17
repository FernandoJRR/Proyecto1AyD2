import type { Entity } from "../utils/entity";

const CURRENT_MEDICINE_URI = "/v1/medicines";

export interface Medicine extends Entity {
  name: string;
  description: string;
  price: number;
  quantity: number;
  minQuantity: number;
}

export interface MedicinePayload {
  name: string;
  description: string;
  price: number;
  quantity: number;
  minQuantity: number;
}

export interface MedicineUpdatePayload {
  name: string | null;
  description: string | null;
  price: number | null;
  quantity: number | null;
  minQuantity: number | null;
}

export const createMedicine = async (data: MedicinePayload) => {
  const response = await $api<Medicine>(`${CURRENT_MEDICINE_URI}/create`, {
    method: "POST",
    body: data,
  });
  return response;
};

export const updateMedicine = async (id: string, data: MedicineUpdatePayload) => {
  const response = await $api<Medicine>(`${CURRENT_MEDICINE_URI}/${id}`, {
    method: "PATCH",
    body: data,
  });
  return response;
}

export const getAllMedicines = async () => {
  const response = await $api<Medicine[]>(`${CURRENT_MEDICINE_URI}/all`);
  return response;
}
