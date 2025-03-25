import { describe, it, expect, beforeAll, beforeEach, vi } from 'vitest'

// Mock de la función $api
const mockApi = vi.fn()

// Sobrescribimos el módulo de plainFetch para que exporte nuestro mock
vi.doMock('~/utils/plainFetch', () => ({
  $api: mockApi,
}))

// Variables para las funciones que vamos a testear
let createSpecialistEmployee: any
let getSpecialistEmployeeById: any
let getSpecialistEmployeeByDpi: any
let updateSpecialistEmployee: any
let getAllSpecialistEmployees: any

// Se importan dinámicamente los métodos después de setear el mock
beforeAll(async () => {
  const specialistEmployeeModule = await import('~/lib/api/admin/specialist-employee')
  createSpecialistEmployee = specialistEmployeeModule.createSpecialistEmployee
  getSpecialistEmployeeById = specialistEmployeeModule.getSpecialistEmployeeById
  getSpecialistEmployeeByDpi = specialistEmployeeModule.getSpecialistEmployeeByDpi
  updateSpecialistEmployee = specialistEmployeeModule.updateSpecialistEmployee
  getAllSpecialistEmployees = specialistEmployeeModule.getAllSpecialistEmployees
})

beforeEach(() => {
  vi.clearAllMocks()
})

describe('SpecialistEmployee API Utilities', () => {

  it('createSpecialistEmployee llama a $api con la URL y payload correctos', async () => {
    const payload = {
      nombres: 'Juan',
      apellidos: 'Pérez',
      dpi: '1234567890101'
    }

    const mockResponse = { id: '1', ...payload }
    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await createSpecialistEmployee(payload)

    expect(mockApi).toHaveBeenCalledWith('/v1/specialist-employees/create', {
      method: 'POST',
      body: payload
    })

    expect(result).toEqual(mockResponse)
  })

  it('getSpecialistEmployeeById llama a $api con el id correcto', async () => {
    const id = 'abc123'
    const mockResponse = { id, nombres: 'Juan', apellidos: 'Pérez', dpi: '1234567890101' }

    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await getSpecialistEmployeeById(id)

    expect(mockApi).toHaveBeenCalledWith(`/v1/specialist-employees/id/${id}`)
    expect(result).toEqual(mockResponse)
  })

  it('getSpecialistEmployeeByDpi llama a $api con el dpi correcto', async () => {
    const dpi = '1234567890101'
    const mockResponse = { id: '1', nombres: 'Juan', apellidos: 'Pérez', dpi }

    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await getSpecialistEmployeeByDpi(dpi)

    expect(mockApi).toHaveBeenCalledWith(`/v1/specialist-employees/dpi/${dpi}`)
    expect(result).toEqual(mockResponse)
  })

  it('updateSpecialistEmployee llama a $api con el id y payload correctos', async () => {
    const id = 'abc123'
    const payload = {
      nombres: 'Carlos',
      apellidos: 'García',
      dpi: null
    }

    const mockResponse = { id, ...payload }
    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await updateSpecialistEmployee(id, payload)

    expect(mockApi).toHaveBeenCalledWith(`/v1/specialist-employees/${id}`, {
      method: 'PATCH',
      body: payload
    })

    expect(result).toEqual(mockResponse)
  })

  it('getAllSpecialistEmployees llama a $api sin query cuando search es null', async () => {
    const mockResponse = [
      { id: '1', nombres: 'Ana', apellidos: 'Gómez', dpi: '1111111110101' }
    ]

    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await getAllSpecialistEmployees(null)

    expect(mockApi).toHaveBeenCalledWith('/v1/specialist-employees/all')
    expect(result).toEqual(mockResponse)
  })

  it('getAllSpecialistEmployees llama a $api con query cuando search no es null', async () => {
    const search = 'Ana'
    const mockResponse = [
      { id: '1', nombres: 'Ana', apellidos: 'Gómez', dpi: '1111111110101' }
    ]

    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await getAllSpecialistEmployees(search)

    expect(mockApi).toHaveBeenCalledWith(`/v1/specialist-employees/all?query=${search}`)
    expect(result).toEqual(mockResponse)
  })

})
