package com.hospitalApi.consults.port;

import java.util.List;

import com.hospitalApi.consults.dtos.CreateConsultRequestDTO;
import com.hospitalApi.consults.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForConsultPort {

        public Consult findById(String id) throws NotFoundException;

        public Consult createConsult(String patientId, Double costoConsulta)
                        throws NotFoundException;

        public Consult updateConsult(String id, UpdateConsultRequestDTO updateConsultRequestDTO)
                        throws NotFoundException;

        public Double obtenerTotalConsulta(String id) throws NotFoundException;

        public Consult pagarConsulta(String id) throws NotFoundException, IllegalStateException;

        public Consult addHabitacionToConsult(String id, String habitacionId)
                        throws NotFoundException, IllegalStateException;

        public List<Consult> getAllConsults();
}