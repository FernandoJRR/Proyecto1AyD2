package com.hospitalApi.consults.services;

import org.springframework.stereotype.Service;

import com.hospitalApi.surgery.ports.ForSurgeryCalculationPort;
import com.hospitalApi.surgery.repositories.SurgeryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class SurgeryCalculationService implements ForSurgeryCalculationPort {

    private final SurgeryRepository surgeryRepository;

    @Override
    public Double totalSurgerisByConsult(String consultId) throws IllegalStateException {
        if (!surgeryRepository.allSurgeriesPerformedByConsultId(consultId)) {
            throw new IllegalStateException(
                    "No se puede calcular el total porque no todas las cirugías han sido realizadas.");
        }
        Double total = surgeryRepository.sumSurgeryCostByConsultId(consultId);
        return total == null ? 0.0 : total;
    }

    @Override
    public Boolean allSurgeriesPerformedByConsultId(String consultId) {
        return surgeryRepository.allSurgeriesPerformedByConsultId(consultId);
    }

    @Override
    public Boolean consultHaveSugeriesPerformed(String consultId) {
        return surgeryRepository.existsByConsultIdAndPerformedDateIsNotNull(consultId);
    }

}
