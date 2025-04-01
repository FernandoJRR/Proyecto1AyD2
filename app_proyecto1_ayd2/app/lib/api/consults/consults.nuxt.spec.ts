import { describe, it, expect, beforeAll, beforeEach, vi } from "vitest";

// Mock de la función $api y genParams
const mockApi = vi.fn();
const mockGenParams = vi.fn(() => "?query=params");

vi.mock("~/utils/plainFetch", () => ({ $api: mockApi }));
vi.mock("~/lib/api/utils/params", () => ({ genParams: mockGenParams }));

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
  it("getAllConsults llama a $api con filtros", async () => {
    const filters = { patientDpi: "123", isPaid: true } as any;
    const mockResponse = [{ id: "c1" }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getAllConsults(filters);

    expect(mockGenParams).toHaveBeenCalledWith(filters);
    expect(mockApi).toHaveBeenCalledWith("/v1/consults/all?query=params");
    expect(result).toEqual(mockResponse);
  });

  it("getConsult llama a $api con ID", async () => {
    mockApi.mockResolvedValueOnce({ id: "c1" });
    const result = await getConsult("c1");

    expect(mockApi).toHaveBeenCalledWith("/v1/consults/c1");
    expect(result.id).toBe("c1");
  });

  it("createConsult llama a $api con POST y datos", async () => {
    const payload = {
      patientId: "p1",
      employeeId: "e1",
      costoConsulta: 100,
    };
    const mockResponse = { id: "newConsult", ...payload };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await createConsult(payload);

    expect(mockApi).toHaveBeenCalledWith("/v1/consults/create", {
      method: "POST",
      body: payload,
    });
    expect(result).toEqual(mockResponse);
  });

  it("updateConsult llama a $api con PATCH", async () => {
    const mockResponse = { id: "c1", costoConsulta: 200 };
    const data = { costoConsulta: 200 };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await updateConsult("c1", data);

    expect(mockApi).toHaveBeenCalledWith("/v1/consults/c1", {
      method: "PATCH",
      body: data,
    });
    expect(result).toEqual(mockResponse);
  });

  it("markConsultAsInternado hace POST", async () => {
    const data = { consultId: "c1", roomId: "r1" };
    const mockResponse = { id: "c1", isInternado: true };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await markConsultAsInternado(data);

    expect(mockApi).toHaveBeenCalledWith("/v1/consults/mark-internado", {
      method: "POST",
      body: data,
    });
    expect(result).toEqual(mockResponse);
  });

  it("payConsult hace POST al endpoint correcto", async () => {
    mockApi.mockResolvedValueOnce({ consultId: "c1", totalCost: 500 });

    const result = await payConsult("c1");

    expect(mockApi).toHaveBeenCalledWith("/v1/consults/pay/c1", {
      method: "POST",
    });
    expect(result.totalCost).toBe(500);
  });

  it("calcTotalConsult llama a /total/:id", async () => {
    mockApi.mockResolvedValueOnce({ consultId: "c1", totalCost: 800 });

    const result = await calcTotalConsult("c1");

    expect(mockApi).toHaveBeenCalledWith("/v1/consults/total/c1");
    expect(result.totalCost).toBe(800);
  });

  it("employeesConsult obtiene lista de empleados de consulta", async () => {
    mockApi.mockResolvedValueOnce([{ employeeId: "e1" }]);

    const result = await employeesConsult("c1");

    expect(mockApi).toHaveBeenCalledWith("/v1/consults/c1/employees");
    expect(result).toHaveLength(1);
  });

  it("addEmployeeToConsult hace POST con los datos", async () => {
    const data = { consultId: "c1", employeeId: "e1" };
    const mockResponse = { employeeId: "e1" };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await addEmployeeToConsult(data);

    expect(mockApi).toHaveBeenCalledWith("/v1/consults/add-employee", {
      method: "POST",
      body: data,
    });
    expect(result).toEqual(mockResponse);
  });

  it("deleteEmployeeFromConsult hace DELETE con los datos", async () => {
    const data = { consultId: "c1", employeeId: "e1" };
    const mockResponse = { success: true };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await deleteEmployeeFromConsult(data);

    expect(mockApi).toHaveBeenCalledWith("/v1/consults/delete-employee", {
      method: "DELETE",
      body: data,
    });
    expect(result).toEqual(mockResponse);
  });
});
