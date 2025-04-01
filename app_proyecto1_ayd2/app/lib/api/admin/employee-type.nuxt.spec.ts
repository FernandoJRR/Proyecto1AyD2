import { describe, it, expect, beforeAll, beforeEach, vi } from 'vitest';

// Mock de $api
const mockApi = vi.fn();

vi.doMock('~/utils/plainFetch', () => ({
  $api: mockApi
}));

let getEmployeeTypeById: any;
let getAllEmployeeTypes: any;
let createEmployeeType: any;
let deleteEmployeeType: any;
let editEmployeeType: any;

beforeAll(async () => {
  const module = await import('~/lib/api/admin/employee-type');
  getEmployeeTypeById = module.getEmployeeTypeById;
  getAllEmployeeTypes = module.getAllEmployeeTypes;
  createEmployeeType = module.createEmployeeType;
  deleteEmployeeType = module.deleteEmployeeType;
  editEmployeeType = module.editEmployeeType;
});

beforeEach(() => {
  vi.clearAllMocks();
});

describe('Employee Type API', () => {
  it('getEmployeeTypeById llama a $api con el id correcto', async () => {
    const id = 'type123';
    const mockResponse = { id, name: 'Administrador', permissions: [] };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getEmployeeTypeById(id);

    expect(mockApi).toHaveBeenCalledWith(`/v1/employee-types/${id}`);
    expect(result).toEqual(mockResponse);
  });

  it('getAllEmployeeTypes llama a $api sin parámetros', async () => {
    const mockResponse = [{ id: '1', name: 'Admin', permissions: [] }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getAllEmployeeTypes();

    expect(mockApi).toHaveBeenCalledWith('/v1/employee-types', { params: undefined });
    expect(result).toEqual(mockResponse);
  });

  it('createEmployeeType llama a $api con método POST y datos correctos', async () => {
    const payload = {
      name: 'Nuevo Tipo',
      permissions: [{ id: 'p1' }, { id: 'p2' }]
    };
    const mockResponse = { id: 'et1', ...payload };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await createEmployeeType(payload);

    expect(mockApi).toHaveBeenCalledWith('/v1/employee-types', {
      method: 'POST',
      body: payload
    });
    expect(result).toEqual(mockResponse);
  });

  it('deleteEmployeeType llama a $api con método DELETE y id correcto', async () => {
    const id = 'et1';
    mockApi.mockResolvedValueOnce(undefined);

    const result = await deleteEmployeeType(id);

    expect(mockApi).toHaveBeenCalledWith(`/v1/employee-types/${id}`, {
      method: 'DELETE'
    });
    expect(result).toBeUndefined();
  });

  it('editEmployeeType llama a $api con método PATCH y datos correctos', async () => {
    const id = 'et1';
    const payload = {
      name: 'Actualizado',
      permissions: [{ id: 'p1' }]
    };
    const mockResponse = { id, ...payload };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await editEmployeeType(id, payload);

    expect(mockApi).toHaveBeenCalledWith(`/v1/employee-types/${id}`, {
      method: 'PATCH',
      body: payload
    });
    expect(result).toEqual(mockResponse);
  });
});
