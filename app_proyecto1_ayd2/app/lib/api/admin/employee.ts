import type { Entity } from "../utils/entity"
import type { EmployeeType } from "./employee-type"

const CURRENT_EMPLOYEE_URI = '/v1/employees'

export interface Employee extends Entity {
  firstName: string
  lastName: string
  salary: number
  iggsPercentage: number
  irtraPercentage: number
  employeeType: EmployeeType
}


export interface UserPayload {
  username: string
  password: string
}


export interface EmployeePayload {
  firstName: string
  lastName: string
  salary: number
  iggsPercentage: number | null
  irtraPercentage: number | null
  employeeTypeId: { id: string }
  createUserRequestDTO: UserPayload
}

export async function getAllEmployees(params?: {}) {
  return await $api<Employee[]>(`${CURRENT_EMPLOYEE_URI}/`, {
    params
  })
}

export async function getEmployeeById(employee_id: string) {
  return await $api<Employee>(`${CURRENT_EMPLOYEE_URI}/${employee_id}`)
}

export const createEmployee = async (data: EmployeePayload) => {
  const response = await $api<Employee>(`${CURRENT_EMPLOYEE_URI}`, {
    method: 'POST',
    body: data
  })
  return response
}
