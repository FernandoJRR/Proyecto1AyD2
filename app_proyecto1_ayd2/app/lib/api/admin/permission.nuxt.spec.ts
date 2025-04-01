import { describe, it, expect, beforeAll, beforeEach, vi } from 'vitest';

// Mock de $api
const mockApi = vi.fn();

vi.doMock('~/utils/plainFetch', () => ({
  $api: mockApi
}));

let getAllPermissions: any;

beforeAll(async () => {
  const module = await import('~/lib/api/admin/permission');
  getAllPermissions = module.getAllPermissions;
});

beforeEach(() => {
  vi.clearAllMocks();
});

describe('Permissions API', () => {
  it('getAllPermissions llama a $api sin parámetros', async () => {
    const mockResponse = [{ id: 'p1', name: 'VER_USUARIOS' }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getAllPermissions();

    expect(mockApi).toHaveBeenCalledWith('/v1/permissions', { params: undefined });
    expect(result).toEqual(mockResponse);
  });

  it('getAllPermissions llama a $api con parámetros', async () => {
    const params = { filter: 'admin' };
    const mockResponse = [{ id: 'p2', name: 'EDITAR_USUARIOS' }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getAllPermissions(params);

    expect(mockApi).toHaveBeenCalledWith('/v1/permissions', { params });
    expect(result).toEqual(mockResponse);
  });
});
