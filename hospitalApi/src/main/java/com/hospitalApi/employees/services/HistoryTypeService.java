package com.hospitalApi.employees.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hospitalApi.employees.models.HistoryType;
import com.hospitalApi.employees.ports.ForHistoryTypePort;
import com.hospitalApi.employees.repositories.HistoryTypeRepository;
import com.hospitalApi.shared.exceptions.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryTypeService implements ForHistoryTypePort {


    private final HistoryTypeRepository historyTypeRepository;
    /**
     *
     * @param historyTypeName
     * @throws NotFoundException si el nombre del tipo empleado no existe
     */
    public HistoryType findHistoryTypeByName(String historyTypeName) throws NotFoundException {
        Optional<HistoryType> foundHistoryType = historyTypeRepository.findByType(historyTypeName);
        if (foundHistoryType.isEmpty()) {
            throw new NotFoundException("No existe el tipo de Historial con el nombre ingresado.");
        }

        return foundHistoryType.get();
    }
}
