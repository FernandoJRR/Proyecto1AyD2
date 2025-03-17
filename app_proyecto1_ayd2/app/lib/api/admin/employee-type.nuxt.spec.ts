// employee-type.nuxt.spec.ts
import { describe, it, expect, beforeAll, beforeEach, vi } from 'vitest'

// Create our mock function.
const mockApi = vi.fn()

// Override the plainFetch module to export our mock as $api.
vi.doMock('~/utils/plainFetch', () => ({
  $api: mockApi,
}))

// Declare variables to hold our dynamically imported modules.
let getAllEmployeeTypes: any
let $api: any

// Dynamically import the employee-type module after setting up the mock.
beforeAll(async () => {
  const employeeTypeModule = await import('~/lib/api/admin/employee-type')
  getAllEmployeeTypes = employeeTypeModule.getAllEmployeeTypes

  const plainFetch = await import('~/utils/plainFetch')
  $api = plainFetch.$api
})

beforeEach(() => {
  vi.clearAllMocks()
})

describe('EmployeeType API Utilities', () => {
  it('getAllEmployeeTypes calls $api with the correct URL and params, and returns employee types array', async () => {
    const mockEmployeeTypes = [
      { name: 'Manager' },
      { name: 'Developer' }
    ]
    // Set up the mock to resolve with our sample data.
    mockApi.mockResolvedValueOnce(mockEmployeeTypes)
    
    const params = { some: 'param' }
    const result = await getAllEmployeeTypes(params)
    
    // Verify that $api was called with the expected URL and options.
    expect(mockApi).toHaveBeenCalledWith('/v1/employee-types', { params })
    // Verify that the function returns the mocked employee types array.
    expect(result).toEqual(mockEmployeeTypes)
  })
})
