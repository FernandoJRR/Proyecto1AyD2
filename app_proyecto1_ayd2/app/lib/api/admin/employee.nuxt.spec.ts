import { describe, it, expect, vi, beforeAll, beforeEach } from 'vitest';

const mockApi = vi.fn();

vi.doMock('~/utils/plainFetch', () => ({
  $api: mockApi
}));

let getAllEmployees: any;
let getEmployeeById: any;
let createEmployee: any;
let updateEmployee: any;
let updateEmployeeSalary: any;
let deactivateEmployee: any;
let reactivateEmployee: any;
let getDoctors: any;
let getEnfermeros: any;

beforeAll(async () => {
  const module = await import('~/lib/api/admin/employee');
  getAllEmployees = module.getAllEmployees;
  getEmployeeById = module.getEmployeeById;
  createEmployee = module.createEmployee;
  updateEmployee = module.updateEmployee;
  updateEmployeeSalary = module.updateEmployeeSalary;
  deactivateEmployee = module.deactivateEmployee;
  reactivateEmployee = module.reactivateEmployee;
  getDoctors = module.getDoctors;
  getEnfermeros = module.getEnfermeros;
});

beforeEach(() => {
  vi.clearAllMocks();
});

describe('Employee API Utilities', () => {
  it('getAllEmployees calls $api with the correct URL and params, and returns employee array', async () => {
    const mockEmployees = [
      {
        firstName: 'John',
        lastName: 'Doe',
        salary: 3000,
        iggsPercentage: 5,
        irtraPercentage: 3,
        employeeType: { name: 'Developer' }
      },
      {
        firstName: 'Jane',
        lastName: 'Smith',
        salary: 4000,
        iggsPercentage: 8,
        irtraPercentage: 2,
        employeeType: { name: 'Manager' }
      }
    ]
    // Set up the mock to resolve with our sample data.
    mockApi.mockResolvedValueOnce(mockEmployees)

    const params = { some: 'param' }
    const result = await getAllEmployees(params)

    // Verify that our mock function was called with the expected URL and options.
    expect(mockApi).toHaveBeenCalledWith('/v1/employees/', { params })
    // Verify that the returned value equals our mock data.
    expect(result).toEqual(mockEmployees)
  })

  it('createEmployee llama a $api con método POST', async () => {
    const payload = {
      firstName: 'Ana',
      lastName: 'Perez',
      salary: 5000,
      igssPercentage: 5,
      irtraPercentage: 2,
      employeeTypeId: { id: '1' },
      createUserRequestDTO: { username: 'bobw', password: 'secret123' }
    }
    const mockEmployee = {
      ...payload,
      employeeType: { name: 'Support' }
    }
    mockApi.mockResolvedValueOnce(mockEmployee)

    const result = await createEmployee(payload)

    expect(mockApi).toHaveBeenCalledWith('/v1/employees', {
      method: 'POST',
      body: payload
    });
    expect(result).toEqual(mockEmployee);
  });

  it('updateEmployee llama a $api con método PATCH y datos correctos', async () => {
    const payload = {
      firstName: 'Ana',
      lastName: 'Ramirez',
      salary: 5500,
      igssPercentage: 5,
      irtraPercentage: 2,
      employeeTypeId: { id: '2' }
    };
    const response = { id: 'emp1', ...payload };
    mockApi.mockResolvedValueOnce(response);

    const result = await updateEmployee(payload, 'emp1');

    expect(mockApi).toHaveBeenCalledWith('/v1/employees/emp1', {
      method: 'PATCH',
      body: payload
    });
    expect(result).toEqual(response);
  });

  it('updateEmployeeSalary llama a $api con método PATCH y datos correctos', async () => {
    const payload = { salary: 6000, salaryDate: new Date('2025-01-01') };
    const response = { id: 'emp1', salary: 6000 };
    mockApi.mockResolvedValueOnce(response);

    const result = await updateEmployeeSalary(payload, 'emp1');

    expect(mockApi).toHaveBeenCalledWith('/v1/employees/emp1/salary', {
      method: 'PATCH',
      body: payload
    });
    expect(result).toEqual(response);
  });

  it('deactivateEmployee llama a $api con método PATCH', async () => {
    const payload = {
      deactivationDate: new Date('2025-04-01'),
      historyTypeId: { id: 'ht1' }
    };
    const response = { id: 'emp1', desactivatedAt: payload.deactivationDate };
    mockApi.mockResolvedValueOnce(response);

    const result = await deactivateEmployee(payload, 'emp1');

    expect(mockApi).toHaveBeenCalledWith('/v1/employees/emp1/desactivate', {
      method: 'PATCH',
      body: payload
    });
    expect(result).toEqual(response);
  });

  it('reactivateEmployee llama a $api con método PATCH', async () => {
    const payload = { reactivationDate: new Date('2025-04-15') };
    const response = { id: 'emp1', desactivatedAt: null };
    mockApi.mockResolvedValueOnce(response);

    const result = await reactivateEmployee(payload, 'emp1');

    expect(mockApi).toHaveBeenCalledWith('/v1/employees/emp1/reactivate', {
      method: 'PATCH',
      body: payload
    });
    expect(result).toEqual(response);
  });

  it('getDoctors llama a $api con parámetro search', async () => {
    const response = [{ id: '1', firstName: 'Doctor' }];
    mockApi.mockResolvedValueOnce(response);

    const result = await getDoctors('doc');

    expect(mockApi).toHaveBeenCalledWith('/v1/employees/doctors', {
      params: { search: 'doc' }
    });
    expect(result).toEqual(response);
  });

  it('getEnfermeros llama a $api con parámetro search', async () => {
    const response = [{ id: '2', firstName: 'Nurse' }];
    mockApi.mockResolvedValueOnce(response);

    const result = await getEnfermeros('nurse');

    expect(mockApi).toHaveBeenCalledWith('/v1/employees/nurses', {
      params: { search: 'nurse' }
    });
    expect(result).toEqual(response);
  });
});
