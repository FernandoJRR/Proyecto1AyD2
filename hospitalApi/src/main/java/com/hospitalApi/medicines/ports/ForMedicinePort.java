package com.hospitalApi.medicines.ports;

import java.util.List;

import com.hospitalApi.medicines.models.Medicine;

public interface ForMedicinePort {
    public Medicine createMedicine(Medicine medicine);

    public Medicine updateMedicine(Long id, Medicine medicine);

    public Medicine getMedicine(Long id);

    public boolean deleteMedicine(Long id);

    public List<Medicine> getAllMedicines();
}
