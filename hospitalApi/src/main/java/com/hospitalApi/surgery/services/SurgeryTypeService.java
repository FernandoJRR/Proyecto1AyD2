package com.hospitalApi.surgery.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.dtos.UpdateSurgeryTypeRequestDTO;
import com.hospitalApi.surgery.models.SurgeryType;
import com.hospitalApi.surgery.ports.ForSurgeryTypePort;
import com.hospitalApi.surgery.repositories.SurgeryTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurgeryTypeService implements ForSurgeryTypePort {

    private final SurgeryTypeRepository surgeryTypeRepository;

    @Override
    public List<SurgeryType> getSurgeryTypes(String search) {
        if (search != null) {
            return surgeryTypeRepository.findByTypeContainingIgnoreCase(search);
        }
        return surgeryTypeRepository.findAll();
    }

    @Override
    public SurgeryType getSurgeryType(String surgeryTypeId) throws NotFoundException {
        return surgeryTypeRepository.findById(surgeryTypeId)
                .orElseThrow(() -> new NotFoundException("No se encontró el tipo de cirugía con id " + surgeryTypeId));
    }

    @Override
    public SurgeryType createSurgeryType(SurgeryType surgeryType) throws DuplicatedEntryException {
        if (surgeryTypeRepository.existsByType(surgeryType.getType())) {
            throw new DuplicatedEntryException("Ya existe un tipo de cirugía con el nombre " + surgeryType.getType());
        }
        return surgeryTypeRepository.save(surgeryType);
    }

    @Override
    public SurgeryType updateSurgeryType(UpdateSurgeryTypeRequestDTO updateSurgeryTypeRequestDTO, String surgeryTypeId)
            throws DuplicatedEntryException, NotFoundException {
        if (surgeryTypeRepository.existsByTypeAndIdNot(updateSurgeryTypeRequestDTO.getType(), surgeryTypeId)) {
            throw new DuplicatedEntryException(
                    "Ya existe un tipo de cirugía con el nombre " + updateSurgeryTypeRequestDTO.getType());
        }
        SurgeryType surgeryType = getSurgeryType(surgeryTypeId);
        surgeryType.updateFromDTO(updateSurgeryTypeRequestDTO);
        return surgeryTypeRepository.save(surgeryType);
    }

}
