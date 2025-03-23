const CURRENT_PATIENT_URI = "/v1/patients";

export interface PatientResponseDTO {
  id: string;
  firstnames: string;
  lastnames: string;
  dpi: string;
}

export interface CreatePatientRequestDTO {
  firstnames: string;
  lastnames: string;
  dpi: string;
}

export interface UpdatePatientRequestDTO {
  firstnames?: string | null;
  lastnames?: string | null;
  dpi?: string | null;
}

export const createPatient = async (data: CreatePatientRequestDTO) => {
  const response = await $api<PatientResponseDTO>(
    `${CURRENT_PATIENT_URI}/create`,
    {
      method: "POST",
      body: data,
    }
  );
  return response;
};

export const getPatient = async (id: string) => {
  const response = await $api<PatientResponseDTO>(
    `${CURRENT_PATIENT_URI}/${id}`
  );
  return response;
};

export const getPatientByDpi = async (dpi: string) => {
  const response = await $api<PatientResponseDTO>(
    `${CURRENT_PATIENT_URI}/dpi/${dpi}`
  );
  return response;
};

export const updatePatient = async (
  id: string,
  data: UpdatePatientRequestDTO
) => {
  const response = await $api<PatientResponseDTO>(
    `${CURRENT_PATIENT_URI}/${id}`,
    {
      method: "PATCH",
      body: data,
    }
  );
  return response;
};
