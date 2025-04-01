package com.hospitalApi.parameters.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hospitalApi.parameters.models.Parameter;
import com.hospitalApi.parameters.ports.ForParameterPort;
import com.hospitalApi.parameters.repositories.ParameterRepository;
import com.hospitalApi.shared.exceptions.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class ParameterService implements ForParameterPort {

    private final ParameterRepository parameterRepository;

    public Parameter findParameterByKey(String key) throws NotFoundException {
        Optional<Parameter> parameterOptional = parameterRepository.findOneByParameterKey(key);

        if (parameterOptional.isEmpty()) {
            throw new NotFoundException("El parametro con la llave ingresada no existe");
        }

        return parameterOptional.get();
    }
}
