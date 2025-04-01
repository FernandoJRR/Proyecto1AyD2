package com.hospitalApi.employees.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hospitalApi.employees.enums.HistoryTypeEnum;
import com.hospitalApi.employees.models.HistoryType;
import com.hospitalApi.employees.ports.ForHistoryTypePort;
import com.hospitalApi.employees.repositories.HistoryTypeRepository;
import com.hospitalApi.shared.exceptions.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class HistoryTypeService implements ForHistoryTypePort {


    private final HistoryTypeRepository historyTypeRepository;
    /**
     *
     * @param historyTypeName
     * @throws NotFoundException si el nombre del tipo empleado no existe
     */
    @Override
    public HistoryType findHistoryTypeByName(String historyTypeName) throws NotFoundException {
        Optional<HistoryType> foundHistoryType = historyTypeRepository.findByType(historyTypeName);
        if (foundHistoryType.isEmpty()) {
            throw new NotFoundException("No existe el tipo de Historial con el nombre ingresado.");
        }

        return foundHistoryType.get();
    }
    @Override
    public HistoryType findHistoryTypeById(String historyTypeId) throws NotFoundException {
        Optional<HistoryType> foundHistoryType = historyTypeRepository.findById(historyTypeId);
        if (foundHistoryType.isEmpty()) {
            throw new NotFoundException("No existe el tipo de Historial con el id ingresado.");
        }

        return foundHistoryType.get();
    }

    /**
     * Obtiene todos los tipos de historia que existen
     */
    @Override
    public List<HistoryType> findAll(){
        return historyTypeRepository.findAll();
    }
    
    @Override
    public List<HistoryType> findDeactivationHistoryTypes() {
        List<HistoryType> deactivationHistoryTypes = new ArrayList<>();
        Optional<HistoryType> firingOptional = historyTypeRepository.findByType(HistoryTypeEnum.DESPIDO.getType());
        Optional<HistoryType> resignOptional = historyTypeRepository.findByType(HistoryTypeEnum.RENUNCIA.getType());
        if (firingOptional.isPresent()) {
            deactivationHistoryTypes.add(firingOptional.get());
        }

        if (resignOptional.isPresent()) {
            deactivationHistoryTypes.add(resignOptional.get());
        }

        return deactivationHistoryTypes;
    }
}
