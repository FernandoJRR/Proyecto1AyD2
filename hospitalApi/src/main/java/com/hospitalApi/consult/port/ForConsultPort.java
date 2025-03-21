package com.hospitalApi.consult.port;

import org.aspectj.weaver.ast.Not;

import com.hospitalApi.consult.dtos.CreateConsultRequestDTO;
import com.hospitalApi.consult.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.consult.models.Consult;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForConsultPort {

    public Consult findById(String id) throws NotFoundException;

    public Consult createConsult(CreateConsultRequestDTO createConsultRequestDTO, String patientId)
            throws NotFoundException;

    public Consult updateConsult(String id, UpdateConsultRequestDTO updateConsultRequestDTO) throws NotFoundException;

}