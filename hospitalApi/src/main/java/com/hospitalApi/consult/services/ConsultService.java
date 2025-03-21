package com.hospitalApi.consult.services;

import org.springframework.stereotype.Service;

import com.hospitalApi.consult.dtos.CreateConsultRequestDTO;
import com.hospitalApi.consult.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.consult.models.Consult;
import com.hospitalApi.consult.port.ForConsultPort;
import com.hospitalApi.consult.repositories.ConsultRepository;
import com.hospitalApi.shared.exceptions.NotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ConsultService implements ForConsultPort {

    private final ConsultRepository consultRepository;

    @Override
    public Consult findById(String id) throws NotFoundException {
        return consultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta con id " + id + " no encontrada"));
    }

    @Override
    public Consult createConsult(CreateConsultRequestDTO createConsultRequestDTO, String paientId)
            throws NotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createConsult'");
    }

    @Override
    public Consult updateConsult(String id, UpdateConsultRequestDTO updateConsultRequestDTO) throws NotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateConsult'");
    }

}
