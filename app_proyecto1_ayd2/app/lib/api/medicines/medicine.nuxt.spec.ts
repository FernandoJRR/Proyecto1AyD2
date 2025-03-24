import { describe, it, expect, beforeAll, beforeEach, vi } from 'vitest'

// Mock de la función $api
const mockApi = vi.fn()

// Mockeamos el módulo plainFetch para exponer el $api mockeado
vi.doMock('~/utils/plainFetch', () => ({
  $api: mockApi,
}))

// Declaración de las funciones a testear
let createMedicine: any
let getMedicine: any
let updateMedicine: any
let getAllMedicines: any
let getAllMedicinesLowStock: any
let ventaVariosFarmacia: any
let mapLineaVentaMedicineToPayloadSaleMedicineFarmacia: any

beforeAll(async () => {
  const medicineModule = await import('~/lib/api/medicines/medicine')

  createMedicine = medicineModule.createMedicine
  getMedicine = medicineModule.getMedicine
  updateMedicine = medicineModule.updateMedicine
  getAllMedicines = medicineModule.getAllMedicines
  getAllMedicinesLowStock = medicineModule.getAllMedicinesLowStock
  ventaVariosFarmacia = medicineModule.ventaVariosFarmacia
  mapLineaVentaMedicineToPayloadSaleMedicineFarmacia = medicineModule.mapLineaVentaMedicineToPayloadSaleMedicineFarmacia
})

beforeEach(() => {
  vi.clearAllMocks()
})

describe('Medicine API Utilities', () => {

  it('createMedicine llama a $api con la URL y payload correctos', async () => {
    const payload = {
      name: 'Paracetamol',
      description: 'Analgesico',
      price: 5.5,
      quantity: 100,
      minQuantity: 10
    }

    const mockResponse = { id: '1', ...payload }
    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await createMedicine(payload)

    expect(mockApi).toHaveBeenCalledWith('/v1/medicines/create', {
      method: 'POST',
      body: payload
    })
    expect(result).toEqual(mockResponse)
  })

  it('getMedicine llama a $api con el id correcto', async () => {
    const id = 'abc123'
    const mockResponse = {
      id,
      name: 'Ibuprofeno',
      description: 'Antiinflamatorio',
      price: 10,
      quantity: 50,
      minQuantity: 5
    }

    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await getMedicine(id)

    expect(mockApi).toHaveBeenCalledWith(`/v1/medicines/${id}`)
    expect(result).toEqual(mockResponse)
  })

  it('updateMedicine llama a $api con el id y payload correctos', async () => {
    const id = 'abc123'
    const payload = {
      name: 'Ibuprofeno',
      description: null,
      price: 12,
      quantity: 60,
      minQuantity: 5
    }

    const mockResponse = { id, ...payload }
    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await updateMedicine(id, payload)

    expect(mockApi).toHaveBeenCalledWith(`/v1/medicines/${id}`, {
      method: 'PATCH',
      body: payload
    })
    expect(result).toEqual(mockResponse)
  })

  it('getAllMedicines llama a $api sin query cuando search es null', async () => {
    const mockResponse = [
      { id: '1', name: 'Paracetamol', description: '', price: 5, quantity: 100, minQuantity: 10 }
    ]

    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await getAllMedicines(null)

    expect(mockApi).toHaveBeenCalledWith('/v1/medicines/all')
    expect(result).toEqual(mockResponse)
  })

  it('getAllMedicines llama a $api con query cuando search tiene valor', async () => {
    const search = 'paracetamol'
    const mockResponse = [
      { id: '1', name: 'Paracetamol', description: '', price: 5, quantity: 100, minQuantity: 10 }
    ]

    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await getAllMedicines(search)

    expect(mockApi).toHaveBeenCalledWith(`/v1/medicines/all?query=${search}`)
    expect(result).toEqual(mockResponse)
  })

  it('getAllMedicinesLowStock llama a $api correctamente', async () => {
    const mockResponse = [
      { id: '1', name: 'Paracetamol', description: '', price: 5, quantity: 5, minQuantity: 10 }
    ]

    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await getAllMedicinesLowStock()

    expect(mockApi).toHaveBeenCalledWith('/v1/medicines/low-stock')
    expect(result).toEqual(mockResponse)
  })

  it('ventaVariosFarmacia llama a $api con el payload correcto', async () => {
    const payload = [
      { medicineId: '1', quantity: 2 },
      { medicineId: '2', quantity: 1 }
    ]

    const mockResponse = [
      { id: '1', name: 'Paracetamol', price: 5, availableQuantity: 100, quantity: 2, total: 10 },
      { id: '2', name: 'Ibuprofeno', price: 10, availableQuantity: 50, quantity: 1, total: 10 }
    ]

    mockApi.mockResolvedValueOnce(mockResponse)

    const result = await ventaVariosFarmacia(payload)

    expect(mockApi).toHaveBeenCalledWith('/v1/sale-medicines/farmacia/varios', {
      method: 'POST',
      body: payload
    })
    expect(result).toEqual(mockResponse)
  })

  it('mapLineaVentaMedicineToPayloadSaleMedicineFarmacia transforma correctamente los datos', () => {
    const lineasVenta = [
      { id: '1', name: 'Paracetamol', price: 5, availableQuantity: 100, quantity: 2, total: 10 },
      { id: '2', name: 'Ibuprofeno', price: 10, availableQuantity: 50, quantity: 1, total: 10 }
    ]

    const expectedPayload = [
      { medicineId: '1', quantity: 2 },
      { medicineId: '2', quantity: 1 }
    ]

    const result = mapLineaVentaMedicineToPayloadSaleMedicineFarmacia(lineasVenta)

    expect(result).toEqual(expectedPayload)
  })

})
