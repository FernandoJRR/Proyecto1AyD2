import { describe, it, expect, beforeAll, beforeEach, vi } from "vitest";

// Mock de la función $api
const mockApi = vi.fn();
vi.doMock("~/utils/plainFetch", () => ({
  $api: mockApi,
}));

let createMedicine: any;
let getMedicine: any;
let updateMedicine: any;
let getAllMedicines: any;
let getAllMedicinesLowStock: any;
let ventaVariosFarmacia: any;
let ventaVariosConsulta: any;
let getSaleMedicinesConsult: any;
let mapLineaVentaMedicineToPayloadSaleMedicineFarmacia: any;
let mapLineaVentaMedicineToPayloadSaleMedicineConsulta: any;

beforeAll(async () => {
  const mod = await import("~/lib/api/medicines/medicine");
  createMedicine = mod.createMedicine;
  getMedicine = mod.getMedicine;
  updateMedicine = mod.updateMedicine;
  getAllMedicines = mod.getAllMedicines;
  getAllMedicinesLowStock = mod.getAllMedicinesLowStock;
  ventaVariosFarmacia = mod.ventaVariosFarmacia;
  ventaVariosConsulta = mod.ventaVariosConsulta;
  getSaleMedicinesConsult = mod.getSaleMedicinesConsult;
  mapLineaVentaMedicineToPayloadSaleMedicineFarmacia = mod.mapLineaVentaMedicineToPayloadSaleMedicineFarmacia;
  mapLineaVentaMedicineToPayloadSaleMedicineConsulta = mod.mapLineaVentaMedicineToPayloadSaleMedicineConsulta;
});

beforeEach(() => {
  vi.clearAllMocks();
});

describe("Medicines API Utilities", () => {
  it("createMedicine hace POST con los datos correctos", async () => {
    const payload = {
      name: "Paracetamol",
      description: "Analgésico",
      price: 10,
      cost: 5,
      quantity: 100,
      minQuantity: 10,
    };
    const mockResponse = { id: "m1", ...payload };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await createMedicine(payload);

    expect(mockApi).toHaveBeenCalledWith("/v1/medicines/create", {
      method: "POST",
      body: payload,
    });
    expect(result).toEqual(mockResponse);
  });

  it("getMedicine consulta por ID", async () => {
    const id = "med1";
    const mockResponse = { id, name: "Ibuprofeno" };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getMedicine(id);

    expect(mockApi).toHaveBeenCalledWith(`/v1/medicines/${id}`);
    expect(result).toEqual(mockResponse);
  });

  it("updateMedicine hace PATCH con los datos correctos", async () => {
    const id = "med1";
    const data = {
      name: "Ibuprofeno",
      description: "Actualizado",
      price: 12,
      cost: 6,
      quantity: 80,
      minQuantity: 5,
    };
    const mockResponse = { id, ...data };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await updateMedicine(id, data);

    expect(mockApi).toHaveBeenCalledWith(`/v1/medicines/${id}`, {
      method: "PATCH",
      body: data,
    });
    expect(result).toEqual(mockResponse);
  });

  it("getAllMedicines devuelve medicamentos con y sin query", async () => {
    mockApi.mockResolvedValueOnce([{ id: "m1" }]);
    await getAllMedicines(null);
    expect(mockApi).toHaveBeenCalledWith("/v1/medicines/all");

    mockApi.mockResolvedValueOnce([{ id: "m2" }]);
    await getAllMedicines("ibuprofeno");
    expect(mockApi).toHaveBeenCalledWith("/v1/medicines/all?query=ibuprofeno");
  });

  it("getAllMedicinesLowStock consulta los de bajo stock", async () => {
    const mockResponse = [{ id: "m1", quantity: 3 }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getAllMedicinesLowStock();

    expect(mockApi).toHaveBeenCalledWith("/v1/medicines/low-stock");
    expect(result).toEqual(mockResponse);
  });

  it("ventaVariosFarmacia realiza POST con los datos de venta", async () => {
    const data = [{ medicineId: "m1", quantity: 2 }];
    const mockResponse = [{ id: "m1", total: 20 }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await ventaVariosFarmacia(data);

    expect(mockApi).toHaveBeenCalledWith("/v1/sale-medicines/farmacia/varios", {
      method: "POST",
      body: data,
    });
    expect(result).toEqual(mockResponse);
  });

  it("ventaVariosConsulta realiza POST con datos correctos", async () => {
    const data = [{ medicineId: "m1", consultId: "c1", quantity: 1 }];
    const mockResponse = [{ id: "m1", total: 10 }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await ventaVariosConsulta(data);

    expect(mockApi).toHaveBeenCalledWith("/v1/sale-medicines/consult/varios", {
      method: "POST",
      body: data,
    });
    expect(result).toEqual(mockResponse);
  });

  it("getSaleMedicinesConsult consulta por ID de consulta", async () => {
    const consultId = "c1";
    const mockResponse = [{ id: "m1", consultId }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getSaleMedicinesConsult(consultId);

    expect(mockApi).toHaveBeenCalledWith(`/v1/sale-medicines/consult/${consultId}`);
    expect(result).toEqual(mockResponse);
  });

  it("mapLineaVentaMedicineToPayloadSaleMedicineFarmacia transforma correctamente", () => {
    const lineas = [{ id: "m1", quantity: 2 }] as any;
    const result = mapLineaVentaMedicineToPayloadSaleMedicineFarmacia(lineas);
    expect(result).toEqual([{ medicineId: "m1", quantity: 2 }]);
  });

  it("mapLineaVentaMedicineToPayloadSaleMedicineConsulta transforma correctamente", () => {
    const lineas = [{ id: "m1", quantity: 2 }] as any;
    const result = mapLineaVentaMedicineToPayloadSaleMedicineConsulta(lineas, "c1");
    expect(result).toEqual([{ medicineId: "m1", consultId: "c1", quantity: 2 }]);
  });
});
