import { describe, it, expect, beforeAll, beforeEach, vi } from "vitest";

// Mock de la función $api
const mockApi = vi.fn();

vi.doMock("~/utils/plainFetch", () => ({
  $api: mockApi,
}));

let getSurgeriesTypes: any;
let updateSurgeryType: any;
let createSurgeryType: any;
let getSurgeryType: any;
let getAllSurgeries: any;
let createSurgery: any;
let deleteSurgery: any;
let markAsCompletedSurgery: any;
let getSurgery: any;
let addDoctorSurgery: any;
let deleteDoctorSurgery: any;
let getAllSugeryEmployees: any;
let getSurgeriesbyConsultId: any;

beforeAll(async () => {
  const module = await import("~/lib/api/surgeries/surgeries");
  getSurgeriesTypes = module.getSurgeriesTypes;
  updateSurgeryType = module.updateSurgeryType;
  createSurgeryType = module.createSurgeryType;
  getSurgeryType = module.getSurgeryType;
  getAllSurgeries = module.getAllSurgeries;
  createSurgery = module.createSurgery;
  deleteSurgery = module.deleteSurgery;
  markAsCompletedSurgery = module.markAsCompletedSurgery;
  getSurgery = module.getSurgery;
  addDoctorSurgery = module.addDoctorSurgery;
  deleteDoctorSurgery = module.deleteDoctorSurgery;
  getAllSugeryEmployees = module.getAllSugeryEmployees;
  getSurgeriesbyConsultId = module.getSurgeriesbyConsultId;
});

beforeEach(() => {
  vi.clearAllMocks();
});

describe("Surgeries API Utilities", () => {
  it("getSurgeriesTypes sin query", async () => {
    const mockResponse = [{ id: "1", type: "Tipo A" }];
    mockApi.mockResolvedValueOnce(mockResponse);
    const result = await getSurgeriesTypes(null);
    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/types/all");
    expect(result).toEqual(mockResponse);
  });

  it("getSurgeriesTypes con query", async () => {
    const result = await getSurgeriesTypes("cardio");
    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/types/all?query=cardio");
  });

  it("updateSurgeryType", async () => {
    const id = "abc";
    const payload = { type: "Nuevo", description: "desc", specialistPayment: 1, hospitalCost: 2, surgeryCost: 3 };
    const resultMock = { id, ...payload };
    mockApi.mockResolvedValueOnce(resultMock);
    const result = await updateSurgeryType(id, payload);
    expect(mockApi).toHaveBeenCalledWith(`/v1/surgeries/types/update/${id}`, {
      method: "PATCH",
      body: payload
    });
    expect(result).toEqual(resultMock);
  });

  it("createSurgeryType", async () => {
    const payload = { type: "Cardio", description: "desc", specialistPayment: 10, hospitalCost: 20, surgeryCost: 30 };
    const mockResponse = { id: "1", ...payload };
    mockApi.mockResolvedValueOnce(mockResponse);
    const result = await createSurgeryType(payload);
    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/types/create", {
      method: "POST",
      body: payload
    });
    expect(result).toEqual(mockResponse);
  });

  it("getSurgeryType", async () => {
    const id = "123";
    const mockResponse = { id, type: "Neuro" };
    mockApi.mockResolvedValueOnce(mockResponse);
    const result = await getSurgeryType(id);
    expect(mockApi).toHaveBeenCalledWith(`/v1/surgeries/types/${id}`);
    expect(result).toEqual(mockResponse);
  });

  it("getAllSurgeries", async () => {
    const mockResponse = [{ id: "s1" }];
    mockApi.mockResolvedValueOnce(mockResponse);
    const result = await getAllSurgeries();
    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/all");
    expect(result).toEqual(mockResponse);
  });

  it("createSurgery", async () => {
    const payload = {
      consultId: "c1",
      surgeryTypeId: "t1",
      asignedDoctorId: "d1",
      isSpecialist: false,
    };
    const mockResponse = { id: "s1", ...payload };
    mockApi.mockResolvedValueOnce(mockResponse);
    const result = await createSurgery(payload);
    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/create", {
      method: "POST",
      body: payload
    });
    expect(result).toEqual(mockResponse);
  });

  it("deleteSurgery", async () => {
    const id = "s1";
    const mockResponse = { surgeryId: id, success: true, message: "ok" };
    mockApi.mockResolvedValueOnce(mockResponse);
    const result = await deleteSurgery(id);
    expect(mockApi).toHaveBeenCalledWith(`/v1/surgeries/${id}`, {
      method: "DELETE"
    });
    expect(result).toEqual(mockResponse);
  });

  it("markAsCompletedSurgery", async () => {
    const id = "s1";
    const mockResponse = { id, performedDate: "2025-04-01" };
    mockApi.mockResolvedValueOnce(mockResponse);
    const result = await markAsCompletedSurgery(id);
    expect(mockApi).toHaveBeenCalledWith(`/v1/surgeries/mark-performed/${id}`, {
      method: "POST"
    });
    expect(result).toEqual(mockResponse);
  });

  it("getSurgery", async () => {
    const id = "s1";
    const mockResponse = { id, consultId: "c1", performedDate: "", surgeryType: {}, surgeryEmployees: [] };
    mockApi.mockResolvedValueOnce(mockResponse);
    const result = await getSurgery(id);
    expect(mockApi).toHaveBeenCalledWith(`/v1/surgeries/${id}`);
    expect(result).toEqual(mockResponse);
  });

  it("addDoctorSurgery", async () => {
    const payload = { surgeryId: "s1", doctorId: "d1", isSpecialist: true };
    const mockResponse = { surgeryId: "s1", specialistEmployeeId: "d1", specialistPayment: 500, employeeName: null, employeeLastName: null };
    mockApi.mockResolvedValueOnce(mockResponse);
    const result = await addDoctorSurgery(payload);
    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/add-employee", {
      method: "POST",
      body: payload
    });
    expect(result).toEqual(mockResponse);
  });

  it("deleteDoctorSurgery", async () => {
    const payload = { surgeryId: "s1", doctorId: "d1", isSpecialist: true };
    const mockResponse = { surgeryId: "s1", specialistEmployeeId: "d1", specialistPayment: 0, employeeName: null, employeeLastName: null };
    mockApi.mockResolvedValueOnce(mockResponse);
    const result = await deleteDoctorSurgery(payload);
    expect(mockApi).toHaveBeenCalledWith("/v1/surgeries/remove-employee", {
      method: "DELETE",
      body: payload
    });
    expect(result).toEqual(mockResponse);
  });

  it("getAllSugeryEmployees", async () => {
    const surgeryId = "s1";
    const mockResponse = [{ surgeryId, employeeId: "d1" }];
    mockApi.mockResolvedValueOnce(mockResponse);
    const result = await getAllSugeryEmployees(surgeryId);
    expect(mockApi).toHaveBeenCalledWith(`/v1/surgeries/surgery-employees/${surgeryId}`);
    expect(result).toEqual(mockResponse);
  });

  it("getSurgeriesbyConsultId", async () => {
    const consultId = "c1";
    const mockResponse = [{ id: "s1", consultId }];
    mockApi.mockResolvedValueOnce(mockResponse);
    const result = await getSurgeriesbyConsultId(consultId);
    expect(mockApi).toHaveBeenCalledWith(`/v1/surgeries/consult/${consultId}`);
    expect(result).toEqual(mockResponse);
  });
});
