// employee.nuxt.spec.ts
import { describe, it, expect, beforeAll, beforeEach, vi } from 'vitest'

// Create our mock function.
const mockApi = vi.fn()

// Use doMock to override the plainFetch module with our mock.
vi.doMock('~/utils/plainFetch', () => ({
  $api: mockApi,
}))

// Declare variables to hold our dynamically imported modules.
let getAllEmployees: any, getEmployeeById: any, createEmployee: any

// Dynamically import the employee module (and the plainFetch module is now mocked).
beforeAll(async () => {
  const employeeModule = await import('~/lib/api/admin/employee')
  getAllEmployees = employeeModule.getAllEmployees
  getEmployeeById = employeeModule.getEmployeeById
  createEmployee = employeeModule.createEmployee
})

beforeEach(() => {
  vi.clearAllMocks()
})

describe('Employee API Utilities', () => {
  it('getAllEmployees calls $api with the correct URL and params, and returns employee array', async () => {
    const mockEmployees = [
      {
        firstName: 'John',
        lastName: 'Doe',
        salary: 3000,
        iggsPercentage: 5,
        irtraPercentage: 3,
        employeeType: { name: 'Developer' }
      },
      {
        firstName: 'Jane',
        lastName: 'Smith',
        salary: 4000,
        iggsPercentage: 8,
        irtraPercentage: 2,
        employeeType: { name: 'Manager' }
      }
    ]
    // Set up the mock to resolve with our sample data.
    mockApi.mockResolvedValueOnce(mockEmployees)
    
    const params = { some: 'param' }
    const result = await getAllEmployees(params)
    
    // Verify that our mock function was called with the expected URL and options.
    expect(mockApi).toHaveBeenCalledWith('/v1/employees/', { params })
    // Verify that the returned value equals our mock data.
    expect(result).toEqual(mockEmployees)
  })

  it('getEmployeeById calls $api with the correct URL and returns employee data', async () => {
    const employeeId = '123'
    const mockEmployee = {
      firstName: 'Alice',
      lastName: 'Johnson',
      salary: 5000,
      iggsPercentage: 10,
      irtraPercentage: 5,
      employeeType: { name: 'Admin' }
    }
    mockApi.mockResolvedValueOnce(mockEmployee)
    
    const result = await getEmployeeById(employeeId)
    
    expect(mockApi).toHaveBeenCalledWith(`/v1/employees/${employeeId}`)
    expect(result).toEqual(mockEmployee)
  })

  it('createEmployee posts data correctly and returns the created employee', async () => {
    const employeePayload = {
      firstName: 'Bob',
      lastName: 'Williams',
      salary: 6000,
      iggsPercentage: 12,
      irtraPercentage: 6,
      employeeTypeId: { id: '1' },
      createUserRequestDTO: { username: 'bobw', password: 'secret123' }
    }
    const mockEmployee = {
      ...employeePayload,
      employeeType: { name: 'Support' }
    }
    mockApi.mockResolvedValueOnce(mockEmployee)
    
    const result = await createEmployee(employeePayload)
    
    expect(mockApi).toHaveBeenCalledWith('/v1/employees', {
      method: 'POST',
      body: employeePayload
    })
    expect(result).toEqual(mockEmployee)
  })
})
