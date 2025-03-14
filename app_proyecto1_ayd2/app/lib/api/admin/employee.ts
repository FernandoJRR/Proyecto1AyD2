import type { Entity } from "../utils/entity"

const CURRENT_EMPLOYEE_URI = '/v1/employees'

export interface Employee extends Entity {
  firstName: string
  lastName: string
  salary: number
  iggsPercentage: number
  irtraPercentage: number
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

export const createEmployee = async (data: EmployeePayload) => {
  const response = await $api<Employee>(`${CURRENT_EMPLOYEE_URI}/create-employee`, {
    method: 'POST',
    body: data
  })
  return response
}
