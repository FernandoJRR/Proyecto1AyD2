import type { Entity } from "../utils/entity";

const CURRENT_HISTORY_TYPE_URI = "/v1/history-types";

export interface HistoryType extends Entity {
  id: string;
  type: string;
}

export async function getDeactivationHistoryTypes(params?: {}) {
  return await $api<HistoryType[]>(`${CURRENT_HISTORY_TYPE_URI}/deactivation`, {
    params,
  });
}

export async function getAllHistoryTypes(params?: {}) {
  return await $api<HistoryType[]>(`${CURRENT_HISTORY_TYPE_URI}/all`, {
    params,
  });
}
