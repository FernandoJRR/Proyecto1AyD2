const CURRENT_SPECIALIST_EMPLOYEE_URI = "/v1/specialist-employees";

export interface SpecialistEmployeeResponseDTO {
  id: string;
  nombres: string;
  apellidos: string;
  dpi: string;
}

export interface CreateSpecialistEmployeeRequestDTO {
  nombres: string;
  apellidos: string;
  dpi: string;
}

export interface UpdateSpecialistEmployeeRequestDTO {
  nombres?: string | null;
  apellidos?: string | null;
  dpi?: string | null;
}

export const createSpecialistEmployee = async (
  data: CreateSpecialistEmployeeRequestDTO
) => {
  const response = await $api<SpecialistEmployeeResponseDTO>(
    `${CURRENT_SPECIALIST_EMPLOYEE_URI}/create`,
    {
      method: "POST",
      body: data,
    }
  );
  return response;
};

export const getSpecialistEmployeeById = async (id: string) => {
  const response = await $api<SpecialistEmployeeResponseDTO>(
    `${CURRENT_SPECIALIST_EMPLOYEE_URI}/id/${id}`
  );
  return response;
};

export const getSpecialistEmployeeByDpi = async (dpi: string) => {
  const response = await $api<SpecialistEmployeeResponseDTO>(
    `${CURRENT_SPECIALIST_EMPLOYEE_URI}/dpi/${dpi}`
  );
  return response;
};

export const updateSpecialistEmployee = async (
  id: string,
  data: UpdateSpecialistEmployeeRequestDTO
) => {
  const response = await $api<SpecialistEmployeeResponseDTO>(
    `${CURRENT_SPECIALIST_EMPLOYEE_URI}/${id}`,
    {
      method: "PATCH",
      body: data,
    }
  );
  return response;
};

export const getAllSpecialistEmployees = async (search: string | null) => {
  let url = `${CURRENT_SPECIALIST_EMPLOYEE_URI}/all`;
  if (search) {
    url = `${url}?query=${search}`;
  }
  const response = await $api<SpecialistEmployeeResponseDTO[]>(url);
  return response;
};
