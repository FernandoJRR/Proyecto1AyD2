import { describe, it, expect, beforeAll, beforeEach, vi } from "vitest";

// Mock de la función $api
const mockApi = vi.fn();

// Mock del módulo plainFetch que exporta $api
vi.doMock("~/utils/plainFetch", () => ({
  $api: mockApi,
}));

// Variables para importar las funciones después de preparar los mocks
let getSurgeriesTypes: any;
let createSurgeryType: any;
let getSurgeryType: any;
let getAllSurgeries: any;
let createSurgery: any;
let getSurgery: any;
let addEmployeeSurgery: any;
let deleteEmployeeSurgery: any;
let addEmployeeSpecialistSurgery: any;
let deleteEmployeeSpecialistSurgery: any;
let getAllSugeryEmployees: any;

beforeAll(async () => {
  const surgeriesModule = await import("~/lib/api/surgeries/surgeries");

  getSurgeriesTypes = surgeriesModule.getSurgeriesTypes;
  createSurgeryType = surgeriesModule.createSurgeryType;
  getSurgeryType = surgeriesModule.getSurgeryType;
  getAllSurgeries = surgeriesModule.getAllSurgeries;
  createSurgery = surgeriesModule.createSurgery;
  getSurgery = surgeriesModule.getSurgery;
  addEmployeeSurgery = surgeriesModule.addEmployeeSurgery;
  deleteEmployeeSurgery = surgeriesModule.deleteEmployeeSurgery;
  addEmployeeSpecialistSurgery = surgeriesModule.addEmployeeSpecialistSurgery;
  deleteEmployeeSpecialistSurgery =
    surgeriesModule.deleteEmployeeSpecialistSurgery;
  getAllSugeryEmployees = surgeriesModule.getAllSugeryEmployees;
});

beforeEach(() => {
  vi.clearAllMocks();
});

describe("Surgeries API Utilities", () => {
  it("getSurgeriesTypes llama a $api con la query vacía", async () => {
    const mockResponse = [{ id: "1", type: "Cardiac" }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getSurgeriesTypes(null);

    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/types/all");
    expect(result).toEqual(mockResponse);
  });

  it("getSurgeriesTypes llama a $api con la query especificada", async () => {
    const mockResponse = [{ id: "1", type: "Cardiac" }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getSurgeriesTypes("search");

    expect(mockApi).toHaveBeenCalledWith(
      "/v1/surgeries/types/all?query=search"
    );
    expect(result).toEqual(mockResponse);
  });

  it("createSurgeryType llama a $api con el body correcto", async () => {
    const payload = {
      type: "Cardiac",
      description: "Cirugía de corazón",
      specialistPayment: 5000,
      hospitalCost: 7000,
      surgeryCost: 12000,
    };

    const mockResponse = { id: "1", ...payload };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await createSurgeryType(payload);

    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/types/create", {
      method: "POST",
      body: payload,
    });

    expect(result).toEqual(mockResponse);
  });

  it("getSurgeryType llama a $api con el id correcto", async () => {
    const id = "type123";
    const mockResponse = { id, type: "Ortopédica" };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getSurgeryType(id);

    expect(mockApi).toHaveBeenCalledWith(`/v1/surgeries/types/${id}`);
    expect(result).toEqual(mockResponse);
  });

  it("getAllSurgeries llama a $api correctamente", async () => {
    const mockResponse = [{ id: "1" }, { id: "2" }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getAllSurgeries();

    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/all");
    expect(result).toEqual(mockResponse);
  });

  it("createSurgery llama a $api con el payload correcto", async () => {
    const payload = {
      consultId: "consult1",
      surgeryTypeId: "surgeryType1",
    };

    const mockResponse = { id: "surgery1", ...payload };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await createSurgery(payload);

    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/create", {
      method: "POST",
      body: payload,
    });

    expect(result).toEqual(mockResponse);
  });

  it("getSurgery llama a $api con el id correcto", async () => {
    const id = "surgery1";
    const mockResponse = { id, consultId: "consult1" };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getSurgery(id);

    expect(mockApi).toHaveBeenCalledWith(`/v1/surgeries/${id}`);
    expect(result).toEqual(mockResponse);
  });

  it("addEmployeeSurgery llama a $api con el body correcto", async () => {
    const payload = { surgeryId: "surgery1", employeeId: "emp1" };
    const mockResponse = { surgeryId: "surgery1", employeeId: "emp1" };

    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await addEmployeeSurgery(payload);

    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/add-employee", {
      method: "POST",
      body: payload,
    });

    expect(result).toEqual(mockResponse);
  });

  it("deleteEmployeeSurgery llama a $api con el body correcto", async () => {
    const payload = { surgeryId: "surgery1", employeeId: "emp1" };
    const mockResponse = { surgeryId: "surgery1", employeeId: "emp1" };

    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await deleteEmployeeSurgery(payload);

    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/remove-employee", {
      method: "DELETE",
      body: payload,
    });

    expect(result).toEqual(mockResponse);
  });

  it("addEmployeeSpecialistSurgery llama a $api con el body correcto", async () => {
    const payload = { surgeryId: "surgery1", employeeId: "spec1" };
    const mockResponse = {
      surgeryId: "surgery1",
      specialistEmployeeId: "spec1",
    };

    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await addEmployeeSpecialistSurgery(payload);

    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/add-specialist", {
      method: "POST",
      body: payload,
    });

    expect(result).toEqual(mockResponse);
  });

  it("deleteEmployeeSpecialistSurgery llama a $api con el body correcto", async () => {
    const payload = { surgeryId: "surgery1", employeeId: "spec1" };
    const mockResponse = {
      surgeryId: "surgery1",
      specialistEmployeeId: "spec1",
    };

    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await deleteEmployeeSpecialistSurgery(payload);

    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/remove-specialist", {
      method: "DELETE",
      body: payload,
    });

    expect(result).toEqual(mockResponse);
  });

  it("getAllSugeryEmployees llama a $api con el surgeryId correcto", async () => {
    const surgeryId = "surgery1";
    const mockResponse = [
      { surgeryId, employeeId: "emp1" },
      { surgeryId, specialistEmployeeId: "spec1" },
    ];

    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getAllSugeryEmployees(surgeryId);

    expect(mockApi).toHaveBeenCalledWith(
      `/v1/surgeries/surgery-employees/${surgeryId}`
    );
    expect(result).toEqual(mockResponse);
  });
});
