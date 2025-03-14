import type { Entity } from "../utils/entity"

const CURRENT_EMPLOYEE_URI = '/v1/employees'

export interface Employee extends Entity {
  firstName: string
  lastName: string
  salary: number
  iggsPercentage: number
  irtraPercentage: number
}

export interface EmployeeType extends Entity {
  name: string
}


export interface UserPayload {
  username: string
  password: string
}


export interface EmployeePayload {
  firstName: string
  lastName: string
  salary: number
  iggsPercentage: number
  irtraPercentage: number
  employeeTypeId: { id: string }
  createUserRequestDTO: UserPayload
}

export async function getAllEmployeeTypes(params?: {}) {
  return await $api<EmployeeType[]>(`${CURRENT_EMPLOYEE_URI}/employee-types`, {
    params
  })
}

export const createEmployee = async (data: EmployeePayload) => {
  const response = await $api<Employee>(`${CURRENT_EMPLOYEE_URI}/create-employee`, {
    method: 'POST',
    body: data
  })
  return response
}
