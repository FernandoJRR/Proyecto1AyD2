import { describe, it, expect, beforeAll, beforeEach, vi } from "vitest";

// Mock de la función $api
const mockApi = vi.fn();

// Mockeamos el módulo plainFetch para exponer el $api mockeado
vi.doMock("~/utils/plainFetch", () => ({
  $api: mockApi,
}));

// Declaración de las funciones a testear
let getAllConsults: any;
let getConsult: any;
let createConsult: any;
let updateConsult: any;
let payConsult: any;
let calcTotalConsult: any;

beforeAll(async () => {
  const consultsModule = await import("~/lib/api/consults/consult");

  getAllConsults = consultsModule.getAllConsults;
  getConsult = consultsModule.getConsult;
  createConsult = consultsModule.createConsult;
  updateConsult = consultsModule.updateConsult;
  payConsult = consultsModule.payConsult;
  calcTotalConsult = consultsModule.calcTotalConsult;
});

beforeEach(() => {
  vi.clearAllMocks();
});

describe("Consults API Utilities", () => {
  it("getAllConsults llama a $api con la URL correcta", async () => {
    const mockResponse = [{ id: "1" }, { id: "2" }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getAllConsults();

    expect(mockApi).toHaveBeenCalledWith("/v1/consults/all");
    expect(result).toEqual(mockResponse);
  });

  it("getConsult llama a $api con el id correcto", async () => {
    const consultId = "abc123";
    const mockResponse = { id: consultId };

    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getConsult(consultId);

    expect(mockApi).toHaveBeenCalledWith(`/v1/consults/${consultId}`);
    expect(result).toEqual(mockResponse);
  });

  it("createConsult llama a $api con el body correcto", async () => {
    const payload = {
      patientId: "pat1",
      costoConsulta: 500,
    };

    const mockResponse = { id: "consult123", ...payload };

    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await createConsult(payload);

    expect(mockApi).toHaveBeenCalledWith("/v1/consults/create", {
      method: "POST",
      body: payload,
    });

    expect(result).toEqual(mockResponse);
  });

  it("updateConsult llama a $api con el id y el body correctos", async () => {
    const consultId = "abc123";
    const payload = {
      isInternado: true,
      costoConsulta: 600,
    };

    const mockResponse = { id: consultId, ...payload };

    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await updateConsult(consultId, payload);

    expect(mockApi).toHaveBeenCalledWith(`/v1/consults/${consultId}`, {
      method: "PATCH",
      body: payload,
    });

    expect(result).toEqual(mockResponse);
  });

  it("payConsult llama a $api con el id correcto", async () => {
    const consultId = "abc123";
    const mockResponse = {
      consultId,
      totalCost: 1200,
    };

    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await payConsult(consultId);

    expect(mockApi).toHaveBeenCalledWith(`/v1/consults/pay/${consultId}`, {
      method: "PATCH",
    });

    expect(result).toEqual(mockResponse);
  });

  it("calcTotalConsult llama a $api con el id correcto", async () => {
    const consultId = "abc123";
    const mockResponse = {
      consultId,
      totalCost: 1500,
    };

    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await calcTotalConsult(consultId);

    expect(mockApi).toHaveBeenCalledWith(`/v1/consults/total/${consultId}`);

    expect(result).toEqual(mockResponse);
  });
});
