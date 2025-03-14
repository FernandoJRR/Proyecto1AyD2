import type { Entity } from "../utils/entity";

const CURRENT_EMPLOYEE_TYPE_URI = '/v1/employeeType'

export interface EmployeeType extends Entity {
  name: string
}

export async function getAllEmployeeTypes(params?: {}) {
  return await $api<EmployeeType[]>(`${CURRENT_EMPLOYEE_TYPE_URI}/get-employees-types`, {
    params
  })
}

