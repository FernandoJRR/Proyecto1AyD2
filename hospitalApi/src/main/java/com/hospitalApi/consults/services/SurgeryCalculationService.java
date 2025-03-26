package com.hospitalApi.consults.services;

import org.springframework.stereotype.Service;

import com.hospitalApi.surgery.ports.ForSurgeryCalculationPort;
import com.hospitalApi.surgery.repositories.SurgeryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurgeryCalculationService implements ForSurgeryCalculationPort {

    private final SurgeryRepository surgeryRepository;

    @Override
    public Double totalSurgerisByConsult(String consultId) {
        Double total = surgeryRepository.sumSurgeryCostByConsultId(consultId);
        return total;
    }

}
