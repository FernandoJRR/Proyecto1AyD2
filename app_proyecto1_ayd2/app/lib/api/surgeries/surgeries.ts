import type { Entity } from "../utils/entity";

const CURRENT_SURGERY_URI = "/v1/surgeries";

export interface SurgeryTypeResponseDTO extends Entity {
  type: string;
  description: string;
  specialistPayment: number;
  hospitalCost: number;
  surgeryCost: number;
}

export interface CreateSurgeryTypeRequest {
  type: string;
  description: string;
  specialistPayment: number;
  hospitalCost: number;
  surgeryCost: number;
}

export interface SurgeryEmployeeResponseDTO {
  surgeryId: string;
  employeeId?: string | null;
  specialistEmployeeId?: string | null;
  specialistPayment: number;
}

export interface SurgeryResponseDTO extends Entity {
  consultId: string;
  hospitalCost: number;
  surgeryCost: number;
  surgeryType: SurgeryTypeResponseDTO;
  surgeryEmployees: SurgeryEmployeeResponseDTO[];
}

export interface CreateSurgeryRequestDTO {
  consultId: string;
  surgeryTypeId: string;
}

export interface AddDeleteEmployeeSurgeryDTO {
  surgeryId: string;
  employeeId: string;
}

export const getSurgeriesTypes = async (query: string | null) => {
  let url = `${CURRENT_SURGERY_URI}/types/all`;
  if (query) {
    url = `${url}?query=${query}`;
  }
  const response = await $api<SurgeryTypeResponseDTO[]>(url);
  return response;
};

export const createSurgeryType = async (data: CreateSurgeryTypeRequest) => {
  const response = await $api<SurgeryTypeResponseDTO>(
    `${CURRENT_SURGERY_URI}/types/create`,
    {
      method: "POST",
      body: data,
    }
  );
  return response;
};

export const getSurgeryType = async (id: string) => {
  const response = await $api<SurgeryTypeResponseDTO>(
    `${CURRENT_SURGERY_URI}/types/${id}`
  );
  return response;
};

export const getAllSurgeries = async () => {
  const response = await $api<SurgeryResponseDTO[]>(
    `${CURRENT_SURGERY_URI}/all`
  );
  return response;
};

export const createSurgery = async (data: CreateSurgeryRequestDTO) => {
  const response = await $api<SurgeryResponseDTO>(
    `${CURRENT_SURGERY_URI}/create`,
    {
      method: "POST",
      body: data,
    }
  );
  return response;
};

export const getSurgery = async (id: string) => {
  const response = await $api<SurgeryResponseDTO>(
    `${CURRENT_SURGERY_URI}/${id}`
  );
  return response;
};

export const addEmployeeSurgery = async (data: AddDeleteEmployeeSurgeryDTO) => {
  const response = await $api<SurgeryEmployeeResponseDTO>(
    `${CURRENT_SURGERY_URI}/add-employee`,
    {
      method: "POST",
      body: data,
    }
  );
  return response;
};

export const deleteEmployeeSurgery = async (
  data: AddDeleteEmployeeSurgeryDTO
) => {
  const response = await $api<SurgeryEmployeeResponseDTO>(
    `${CURRENT_SURGERY_URI}/remove-employee`,
    {
      method: "DELETE",
      body: data,
    }
  );
  return response;
};

export const addEmployeeSpecialistSurgery = async (
  data: AddDeleteEmployeeSurgeryDTO
) => {
  const response = await $api<SurgeryEmployeeResponseDTO>(
    `${CURRENT_SURGERY_URI}/add-specialist`,
    {
      method: "POST",
      body: data,
    }
  );
  return response;
};

export const deleteEmployeeSpecialistSurgery = async (
  data: AddDeleteEmployeeSurgeryDTO
) => {
  const response = await $api<SurgeryEmployeeResponseDTO>(
    `${CURRENT_SURGERY_URI}/remove-specialist`,
    {
      method: "DELETE",
      body: data,
    }
  );
  return response;
};

export const getAllSugeryEmployees = async (surgeryId: string) => {
  const response = await $api<SurgeryEmployeeResponseDTO[]>(
    `${CURRENT_SURGERY_URI}/surgery-employees/${surgeryId}`
  );
  return response;
};
