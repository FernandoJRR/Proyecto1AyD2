import { describe, it, expect, beforeAll, beforeEach, vi } from 'vitest'

// Mock de la función $api
const mockApi = vi.fn()

// Mockeamos el módulo plainFetch para exponer el $api mockeado
vi.doMock('~/utils/plainFetch', () => ({
  $api: mockApi
}))

// Declaración de las funciones a testear
let createPatient: any
let getPatient: any
let getPatientByDpi: any
let updatePatient: any

beforeAll(async () => {
  const patientsModule = await import('~/lib/api/patients/patients')

  createPatient = patientsModule.createPatient
  getPatient = patientsModule.getPatient
  getPatientByDpi = patientsModule.getPatientByDpi
  updatePatient = patientsModule.updatePatient
})

beforeEach(() => {
  vi.clearAllMocks()
})

describe('Patients API Utilities', () => {

  it('createPatient llama a $api con la URL y payload correctos', async () => {
    const payload = {
      firstnames: 'Carlos',
      lastnames: 'Ramirez',
      dpi: '1234567890101'
    }

    const mockResponse = { id: '1', ...payload }
    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await createPatient(payload)

    expect(mockApi).toHaveBeenCalledWith('/v1/patients/create', {
      method: 'POST',
      body: payload
    })
    expect(result).toEqual(mockResponse)
  })

  it('getPatient llama a $api con el id correcto', async () => {
    const id = 'abc123'
    const mockResponse = {
      id,
      firstnames: 'Ana',
      lastnames: 'Lopez',
      dpi: '9876543210101'
    }

    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await getPatient(id)

    expect(mockApi).toHaveBeenCalledWith(`/v1/patients/${id}`)
    expect(result).toEqual(mockResponse)
  })

  it('getPatientByDpi llama a $api con el dpi correcto', async () => {
    const dpi = '9876543210101'
    const mockResponse = {
      id: 'def456',
      firstnames: 'Jose',
      lastnames: 'Perez',
      dpi
    }

    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await getPatientByDpi(dpi)

    expect(mockApi).toHaveBeenCalledWith(`/v1/patients/dpi/${dpi}`)
    expect(result).toEqual(mockResponse)
  })

  it('updatePatient llama a $api con el id y payload correctos', async () => {
    const id = 'abc123'
    const payload = {
      firstnames: 'Carlos',
      lastnames: 'Ramirez',
      dpi: '2223334445556'
    }

    const mockResponse = { id, ...payload }

    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await updatePatient(id, payload)

    expect(mockApi).toHaveBeenCalledWith(`/v1/patients/${id}`, {
      method: 'PATCH',
      body: payload
    })

    expect(result).toEqual(mockResponse)
  })
})
