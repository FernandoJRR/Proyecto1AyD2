import type { Entity } from "../utils/entity";

const CURRENT_MEDICINE_URI = "/v1/medicines";
const CURRENT_SALE_MEDICINE_URI = "/v1/sale-medicines";

export interface Medicine extends Entity {
  name: string;
  description: string;
  price: number;
  cost: number;
  quantity: number;
  minQuantity: number;
}

export interface PayloadSaleMedicineFarmacia {
  medicineId: string;
  quantity: number;
}

export interface LineaVentaMedicine {
  id: string;
  name: string;
  price: number;
  availableQuantity: number;
  quantity: number;
  total: number;
}

export const mapLineaVentaMedicineToPayloadSaleMedicineFarmacia = (
  lineasVenta: LineaVentaMedicine[]
) => {
  return lineasVenta.map((lineaVenta) => ({
    medicineId: lineaVenta.id,
    quantity: lineaVenta.quantity,
  }));
};

export interface MedicinePayload {
  name: string;
  description: string;
  price: number;
  cost: number;
  quantity: number;
  minQuantity: number;
}


export const createMedicine = async (data: MedicinePayload) => {
  const response = await $api<Medicine>(`${CURRENT_MEDICINE_URI}/create`, {
    method: "POST",
    body: data,
  });
  return response;
};

export const getMedicine = async (id: string) => {
  const response = await $api<Medicine>(`${CURRENT_MEDICINE_URI}/${id}`);
  return response;
};

export const updateMedicine = async (
  id: string,
  data: MedicinePayload
) => {
  const response = await $api<Medicine>(`${CURRENT_MEDICINE_URI}/${id}`, {
    method: "PATCH",
    body: data,
  });
  return response;
};

export const getAllMedicines = async (search: string | null) => {
  let url = `${CURRENT_MEDICINE_URI}/all`;
  if (search) {
    url = `${url}?query=${search}`;
  }
  const response = await $api<Medicine[]>(url);
  return response;
};

export const getAllMedicinesLowStock = async () => {
  const response = await $api<Medicine[]>(`${CURRENT_MEDICINE_URI}/low-stock`);
  return response;
};

export const ventaVariosFarmacia = async (
  data: PayloadSaleMedicineFarmacia[]
) => {
  const response = await $api<LineaVentaMedicine[]>(
    `${CURRENT_SALE_MEDICINE_URI}/farmacia/varios`,
    {
      method: "POST",
      body: data,
    }
  );
  return response;
};
