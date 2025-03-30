import { type PatientResponseDTO } from "../patients/patients";

const CURRENT_CONSULT_URI = "/v1/consults";

export interface AddDeleteEmployeeConsultRequestDTO {
  consultId: string;
  employeeId: string;
}

export interface ConsultFilterDTO {
  patientId: string | null;
  patientDpi: string | null;
  patientFirstnames: string | null;
  patientLastnames: string | null;
  employeeId: string | null;
  employeeFirstName: string | null;
  employeeLastName: string | null;
  consultId: string | null;
  isPaid: boolean | null;
  isInternado: boolean | null;
}

export interface ConsultResponseDTO {
  id: string;
  patient: PatientResponseDTO;
  isInternado: boolean;
  isPaid: boolean;
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
  costoConsulta: number | null;
}

export interface MarkConsultAsInternadoDTO {
  consultId: string;
  roomId: string;
}

export interface TotalConsultResponseDTO {
  consultId: string;
  totalCost: number;
}

export interface EmployeeConsultResponseDTO {
  employeeId: string;
  employeeName: string;
  employeeLastName: string;
  employeeType: string;
}

export const getAllConsults = async (filters: ConsultFilterDTO) => {
  const response = await $api<ConsultResponseDTO[]>(
    `${CURRENT_CONSULT_URI}/all${genParams(filters)}`
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

export const markConsultAsInternado = async (
  data: MarkConsultAsInternadoDTO
) => {
  const response = await $api<ConsultResponseDTO>(
    `${CURRENT_CONSULT_URI}/mark-internado`,
    {
      method: "POST",
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

export const employeesConsult = async (consultId: string) => {
  const response = await $api<EmployeeConsultResponseDTO[]>(
    `${CURRENT_CONSULT_URI}/${consultId}/employees`
  );
  return response;
};

export const addEmployeeToConsult = async (
  data: AddDeleteEmployeeConsultRequestDTO
) => {
  const response = await $api<EmployeeConsultResponseDTO>(
    `${CURRENT_CONSULT_URI}/add-employee`,
    {
      method: "POST",
      body: data,
    }
  );
  return response;
};

export const deleteEmployeeFromConsult = async (
  data: AddDeleteEmployeeConsultRequestDTO
) => {
  const response = await $api<EmployeeConsultResponseDTO>(
    `${CURRENT_CONSULT_URI}/delete-employee`,
    {
      method: "DELETE",
      body: data,
    }
  );
  return response;
};
