package com.hospitalApi.medicines.services;

import org.springframework.stereotype.Service;

import com.hospitalApi.medicines.ports.ForSaleMedicineCalculationPort;
import com.hospitalApi.medicines.repositories.SaleMedicineRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaleMedicineCalculationService implements ForSaleMedicineCalculationPort {

    private final SaleMedicineRepository saleMedicineRepository;

    @Override
    public Double totalSalesMedicinesByConsult(String consultId) {
        Double total = saleMedicineRepository.totalSalesMedicinesByConsult(consultId);
        return total == null ? 0.0 : total;
    }

}
