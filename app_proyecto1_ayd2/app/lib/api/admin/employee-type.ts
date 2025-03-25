import type { Entity } from "../utils/entity";
import type { Permission } from "./permission";

const CURRENT_EMPLOYEE_TYPE_URI = "/v1/employee-types";

export interface EmployeeType extends Entity {
  id: string;
  name: string;
  permissions: Permission[];
}

export interface EmployeeTypePayLoad {
  name: string;
  permissions: { id: string }[];
}

export async function getEmployeeTypeById(employeeType_id: string) {
  return await $api<EmployeeType>(
    `${CURRENT_EMPLOYEE_TYPE_URI}/${employeeType_id}`
  );
}

export async function getAllEmployeeTypes(params?: {}) {
  return await $api<EmployeeType[]>(`${CURRENT_EMPLOYEE_TYPE_URI}`, {
    params,
  });
}

export const createEmployeeType = async (data: EmployeeTypePayLoad) => {
  const response = await $api<EmployeeType>(`${CURRENT_EMPLOYEE_TYPE_URI}`, {
    method: "POST",
    body: data,
  });
  return response;
};



export const deleteEmployeeType = async (id:string) => {
  const response = await $api<void>(`${CURRENT_EMPLOYEE_TYPE_URI}/${id}`, {
    method: "DELETE"
  });
  return response;
};


export const editEmployeeType = async (id: string, data: EmployeeTypePayLoad) => {
  const response = await $api<EmployeeType>(`${CURRENT_EMPLOYEE_TYPE_URI}/${id}`, {
    method: "PATCH",
    body: data,
  });
  return response;
};
