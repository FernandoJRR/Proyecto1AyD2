package com.hospitalApi.medicines.ports;

import java.util.List;

import com.hospitalApi.medicines.dtos.CreateMedicineRequestDTO;
import com.hospitalApi.medicines.dtos.UpdateMedicineRequestDTO;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForMedicinePort {
    public Medicine createMedicine(CreateMedicineRequestDTO createMedicineRequestDTO) throws DuplicatedEntryException;

    public Medicine updateMedicine(Long id, UpdateMedicineRequestDTO updateMedicineRequestDTO) throws DuplicatedEntryException, NotFoundException;

    public Medicine getMedicine(Long id) throws NotFoundException;

    public boolean deleteMedicine(Long id);

    public List<Medicine> getAllMedicines();
}
