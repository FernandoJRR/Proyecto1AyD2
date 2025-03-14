package com.hospitalApi.medicines.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.ports.ForMedicinePort;
import com.hospitalApi.medicines.repositories.MedicineRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicineService implements ForMedicinePort {

    private final MedicineRepository medicineRepository;

    @Override
    public Medicine createMedicine(Medicine medicine) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createMedicine'");
    }

    @Override
    public Medicine updateMedicine(Long id, Medicine medicine) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateMedicine'");
    }

    @Override
    public Medicine getMedicine(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMedicine'");
    }

    @Override
    public boolean deleteMedicine(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteMedicine'");
    }

    @Override
    public List<Medicine> getAllMedicines() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllMedicines'");
    }

}
