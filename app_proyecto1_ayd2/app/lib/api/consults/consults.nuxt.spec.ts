import { describe, it, expect, beforeAll, beforeEach, vi } from "vitest";

// Mocks
const mockApi = vi.fn();
const mockGenParams = vi.fn(() => "?consultId=1&isPaid=true");

// Mock completo del módulo plainFetch
vi.mock("~/utils/plainFetch", () => ({
  $api: mockApi,
  genParams: mockGenParams,
}));

let getAllConsults: any;
let getConsult: any;
let createConsult: any;
let updateConsult: any;
let markConsultAsInternado: any;
let payConsult: any;
let calcTotalConsult: any;
let employeesConsult: any;
let addEmployeeToConsult: any;
let deleteEmployeeFromConsult: any;

beforeAll(async () => {
  const module = await import("~/lib/api/consults/consult");
  getAllConsults = module.getAllConsults;
  getConsult = module.getConsult;
  createConsult = module.createConsult;
  updateConsult = module.updateConsult;
  markConsultAsInternado = module.markConsultAsInternado;
  payConsult = module.payConsult;
  calcTotalConsult = module.calcTotalConsult;
  employeesConsult = module.employeesConsult;
  addEmployeeToConsult = module.addEmployeeToConsult;
  deleteEmployeeFromConsult = module.deleteEmployeeFromConsult;
});

beforeEach(() => {
  vi.clearAllMocks();
});

describe("Consults API", () => {
  it("getAllConsults llama a $api con filtros y genParams", async () => {
    const filters = { consultId: "1", isPaid: true };
    const mockResponse = [{ id: "1" }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getAllConsults(filters);

    expect(mockGenParams).toHaveBeenCalledWith(filters);
    expect(mockApi).toHaveBeenCalledWith("/v1/consults/all?consultId=1&isPaid=true");
    expect(result).toEqual(mockResponse);
  });

  it("getConsult llama a $api con el id correcto", async () => {
    const mock = { id: "1" };
    mockApi.mockResolvedValueOnce(mock);

    const result = await getConsult("1");
    expect(mockApi).toHaveBeenCalledWith("/v1/consults/1");
    expect(result).toEqual(mock);
  });

  it("createConsult llama a $api con POST y body correcto", async () => {
    const payload = { patientId: "p1", costoConsulta: 100, employeeId: "e1" };
    const mock = { id: "1", ...payload };
    mockApi.mockResolvedValueOnce(mock);

    const result = await createConsult(payload);
    expect(mockApi).toHaveBeenCalledWith("/v1/consults/create", {
      method: "POST",
      body: payload
    });
    expect(result).toEqual(mock);
  });

  it("updateConsult llama a $api con PATCH y body", async () => {
    const payload = { costoConsulta: 200 };
    const mock = { id: "1", ...payload };
    mockApi.mockResolvedValueOnce(mock);

    const result = await updateConsult("1", payload);
    expect(mockApi).toHaveBeenCalledWith("/v1/consults/1", {
      method: "PATCH",
      body: payload
    });
    expect(result).toEqual(mock);
  });

  it("markConsultAsInternado hace POST con datos", async () => {
    const payload = { consultId: "1", roomId: "101" };
    const mock = { id: "1", ...payload };
    mockApi.mockResolvedValueOnce(mock);

    const result = await markConsultAsInternado(payload);
    expect(mockApi).toHaveBeenCalledWith("/v1/consults/mark-internado", {
      method: "POST",
      body: payload
    });
    expect(result).toEqual(mock);
  });

  it("payConsult hace POST a /pay/:id", async () => {
    const mock = { consultId: "1", totalCost: 200 };
    mockApi.mockResolvedValueOnce(mock);

    const result = await payConsult("1");
    expect(mockApi).toHaveBeenCalledWith("/v1/consults/pay/1", {
      method: "POST"
    });
    expect(result).toEqual(mock);
  });

  it("calcTotalConsult llama a $api a /total/:id", async () => {
    const mock = { consultId: "1", totalCost: 300 };
    mockApi.mockResolvedValueOnce(mock);

    const result = await calcTotalConsult("1");
    expect(mockApi).toHaveBeenCalledWith("/v1/consults/total/1");
    expect(result).toEqual(mock);
  });

  it("employeesConsult llama a $api a /:id/employees", async () => {
    const mock = [{ employeeId: "e1" }];
    mockApi.mockResolvedValueOnce(mock);

    const result = await employeesConsult("1");
    expect(mockApi).toHaveBeenCalledWith("/v1/consults/1/employees");
    expect(result).toEqual(mock);
  });

  it("addEmployeeToConsult hace POST con body", async () => {
    const payload = { consultId: "1", employeeId: "e1" };
    const mock = { employeeId: "e1" };
    mockApi.mockResolvedValueOnce(mock);

    const result = await addEmployeeToConsult(payload);
    expect(mockApi).toHaveBeenCalledWith("/v1/consults/add-employee", {
      method: "POST",
      body: payload
    });
    expect(result).toEqual(mock);
  });

  it("deleteEmployeeFromConsult hace DELETE con body", async () => {
    const payload = { consultId: "1", employeeId: "e1" };
    const mock = { employeeId: "e1" };
    mockApi.mockResolvedValueOnce(mock);

    const result = await deleteEmployeeFromConsult(payload);
    expect(mockApi).toHaveBeenCalledWith("/v1/consults/delete-employee", {
      method: "DELETE",
      body: payload
    });
    expect(result).toEqual(mock);
  });
});
