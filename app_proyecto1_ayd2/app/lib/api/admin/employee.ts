import type { Entity } from "../utils/entity"
import type { EmployeeType } from "./employee-type"

const CURRENT_EMPLOYEE_URI = '/v1/employees'

export interface HistoryType {
  type: string
}

export interface EmployeeHistory {
  historyType: HistoryType
  commentary: string
  historyDate: string
}

export interface Employee extends Entity {
  firstName: string
  lastName: string
  salary: number
  iggsPercentage: number
  irtraPercentage: number
  employeeType: EmployeeType
}

export interface EmployeeResponse {
  employeeResponseDTO: Employee,
  username: string,
  employeeHistories: Array<EmployeeHistory>
}

export interface UserPayload {
  username: string
  password: string
}

export interface EmployeeHistoryDatePayload {
  historyDate: string
}

export interface EmployeePayload {
  firstName: string
  lastName: string
  salary: number
  iggsPercentage: number | null
  irtraPercentage: number | null
  employeeTypeId: { id: string }
  createUserRequestDTO: UserPayload
  employeeHistoryDateRequestDTO: EmployeeHistoryDatePayload
}

export interface EmployeeSalaryUpdatePayload {
  salary: number
  salaryDate: Date
}

export async function getAllEmployees(params?: {}) {
  return await $api<Employee[]>(`${CURRENT_EMPLOYEE_URI}/`, {
    params
  })
}

export async function getEmployeeById(employee_id: string) {
  return await $api<EmployeeResponse>(`${CURRENT_EMPLOYEE_URI}/${employee_id}`)
}

export const createEmployee = async (data: EmployeePayload) => {
  const response = await $api<Employee>(`${CURRENT_EMPLOYEE_URI}`, {
    method: 'POST',
    body: data
  })
  return response
}

export const updateEmployeeSalary = async (data: EmployeeSalaryUpdatePayload, employeeId: string) => {
  const response = await $api<Employee>(`${CURRENT_EMPLOYEE_URI}/${employeeId}/salary`, {
    method: 'PATCH',
    body: data
  })
  return response
}
