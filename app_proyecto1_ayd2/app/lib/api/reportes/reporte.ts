import type { Medicine } from "../medicines/medicine";

const CURRENT_REPORTS_URI = "/v1/reports";

export async function getMedicinesReport(medicineName?: string) {
  const params = {
    name: medicineName,
  };
  return await $api<Medicine[]>(`${CURRENT_REPORTS_URI}/getMedicationReport`, {
    params,
  });
}

export async function getMedicationProfitReport(
  medicineName?: string,
  startDate?: Date | null,
  endDate?: Date | null
) {
  const params = {
    name: medicineName,
    startDate: startDate ? startDate.toISOString().split("T")[0] : null,
    endDate: endDate ? endDate.toISOString().split("T")[0] : null,
  };
  return await $api<any>(`${CURRENT_REPORTS_URI}/getMedicationProfitReport`, {
    params,
  });
}

export async function getEmployeeProfitReport(
  employeeName?: string,
  employeeCui?: string
) {
  const params = {
    employeeName: employeeName,
    employeeCui: employeeCui,
  };
  return await $api<any>(`${CURRENT_REPORTS_URI}/getEmployeeProfitReport`, {
    params,
  });
}

export async function getEmployeeLifecycleReport(
   employeTypeId?:string,
   startDate?: Date | null,
   endDate?: Date | null,
   historyTypeIds?:string[]
) {
  const params = {
    employeTypeId:employeTypeId,
    startDate: startDate ? startDate.toISOString().split("T")[0] : null,
    endDate: endDate ? endDate.toISOString().split("T")[0] : null,
    historyTypeIds:historyTypeIds
  };
  return await $api<any>(`${CURRENT_REPORTS_URI}/getEmployeeLifecycleReport`, {
    params,
  });
}

