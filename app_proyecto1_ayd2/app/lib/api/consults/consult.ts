import { type PatientResponseDTO } from "../patients/patients";

const CURRENT_CONSULT_URI = "/v1/consults";

export interface ConsultResponseDTO {
  id: string;
  patient: PatientResponseDTO;
  isInternado: boolean;
  costoConsulta: number;
  costoTotal: number;
  createdAt: string;
  updateAt: string;
}

export interface CreateConsultRequestDTO {
  patientId: string;
  costoConsulta: number;
  employeeId: string;
}

export interface UpdateConsultRequestDTO {
  isInternado?: boolean | null;
  costoConsulta?: number | null;
}

export interface TotalConsultResponseDTO {
  consultId: string;
  totalCost: number;
}

export const getAllConsults = async () => {
  const response = await $api<ConsultResponseDTO[]>(
    `${CURRENT_CONSULT_URI}/all`
  );
  return response;
};

export const getConsult = async (id: string) => {
  const response = await $api<ConsultResponseDTO>(
    `${CURRENT_CONSULT_URI}/${id}`
  );
  return response;
};

export const createConsult = async (data: CreateConsultRequestDTO) => {
  const response = await $api<ConsultResponseDTO>(
    `${CURRENT_CONSULT_URI}/create`,
    {
      method: "POST",
      body: data,
    }
  );
  return response;
};

export const updateConsult = async (
  id: string,
  data: UpdateConsultRequestDTO
) => {
  const response = await $api<ConsultResponseDTO>(
    `${CURRENT_CONSULT_URI}/${id}`,
    {
      method: "PATCH",
      body: data,
    }
  );
  return response;
};

export const payConsult = async (consultId: string) => {
  const response = await $api<TotalConsultResponseDTO>(
    `${CURRENT_CONSULT_URI}/pay/${consultId}`,
    {
      method: "PATCH",
    }
  );
  return response;
};

export const calcTotalConsult = async (consultId: string) => {
  const response = await $api<TotalConsultResponseDTO>(
    `${CURRENT_CONSULT_URI}/total/${consultId}`
  );
  return response;
};
