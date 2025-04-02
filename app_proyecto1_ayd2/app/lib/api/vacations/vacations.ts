import type { VacationEmployee } from "../admin/employee";

const CURRENT_VACATIONS_URI = "/v1/vacations";

export interface VacationDaysResponse {
  days: number
}

export interface PeriodPayload {
  beginDate: Date,
  endDate: Date
}

export interface CreateVacationsPayload {
  periods: PeriodPayload[]
}

export async function getVacationDays() {
  return await $api<VacationDaysResponse>(
    `${CURRENT_VACATIONS_URI}/vacation-days`
  );
}

export const createVacations = async (data: CreateVacationsPayload, employeeId: string, periodYear: number) => {
  const response = await $api<VacationEmployee[]>(`${CURRENT_VACATIONS_URI}/${employeeId}/${periodYear}`, {
    method: "POST",
    body: data.periods,
  });
  return response;
};

export const updateVacations = async (data: CreateVacationsPayload, employeeId: string, periodYear: number) => {
  const response = await $api<VacationEmployee[]>(`${CURRENT_VACATIONS_URI}/${employeeId}/${periodYear}`, {
    method: "PATCH",
    body: data.periods,
  });
  return response;
};

export const updateMarkAsUsed = async (vacationsId: string) => {
  const response = await $api<VacationEmployee[]>(`${CURRENT_VACATIONS_URI}/${vacationsId}/state`, {
    method: "PATCH",
  });
  return response;
};
