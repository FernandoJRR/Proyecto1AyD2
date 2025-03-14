package com.hospitalApi.medicines.services;

import org.springframework.stereotype.Service;

import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.ports.ForMedicinePort;
import com.hospitalApi.medicines.repositories.MedicineRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicineService {
    
    private final MedicineRepository medicineRepository;
    private final ForMedicinePort forMedicinePort;
    

    public Medicine createMedicine(Medicine medicine) {
        return forMedicinePort.createMedicine(medicine);
    }

    public Medicine updateMedicine(Long id, Medicine medicine) {
        return forMedicinePort.updateMedicine(id, medicine);
    }

    public Medicine getMedicine(Long id) {
        return forMedicinePort.getMedicine(id);
    }

    public boolean deleteMedicine(Long id) {
        return forMedicinePort.deleteMedicine(id);
    }
}
