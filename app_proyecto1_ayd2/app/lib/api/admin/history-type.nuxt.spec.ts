import { describe, it, expect, beforeAll, beforeEach, vi } from 'vitest'

// Mock de $api
const mockApi = vi.fn()

vi.doMock('~/utils/plainFetch', () => ({
  $api: mockApi
}))

let getDeactivationHistoryTypes: any

beforeAll(async () => {
  const module = await import('~/lib/api/admin/history-type')
  getDeactivationHistoryTypes = module.getDeactivationHistoryTypes
})

beforeEach(() => {
  vi.clearAllMocks()
})

describe('History Types API', () => {
  it('getDeactivationHistoryTypes llama a $api sin parámetros', async () => {
    const mockResponse = [{ id: 'h1', type: 'Renuncia' }]
    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await getDeactivationHistoryTypes()

    expect(mockApi).toHaveBeenCalledWith('/v1/history-types/deactivation', {
      params: undefined
    })
    expect(result).toEqual(mockResponse)
  })

  it('getDeactivationHistoryTypes llama a $api con parámetros', async () => {
    const params = { search: 'despido' }
    const mockResponse = [{ id: 'h2', type: 'Despido Justificado' }]
    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await getDeactivationHistoryTypes(params)

    expect(mockApi).toHaveBeenCalledWith('/v1/history-types/deactivation', {
      params
    })
    expect(result).toEqual(mockResponse)
  })
})
