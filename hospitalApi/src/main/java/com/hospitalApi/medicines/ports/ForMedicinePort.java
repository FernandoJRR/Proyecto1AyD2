package com.hospitalApi.medicines.ports;

import java.util.List;

import com.hospitalApi.medicines.dtos.CreateMedicineRequestDTO;
import com.hospitalApi.medicines.dtos.MedicineResponseDTO;
import com.hospitalApi.medicines.dtos.UpdateMedicineRequestDTO;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForMedicinePort {
    public Medicine createMedicine(CreateMedicineRequestDTO createMedicineRequestDTO) throws DuplicatedEntryException;

    public Medicine updateMedicine(String id, UpdateMedicineRequestDTO updateMedicineRequestDTO) throws DuplicatedEntryException, NotFoundException;

    public Medicine getMedicine(String id) throws NotFoundException;

    public boolean deleteMedicine(String id);

    public List<Medicine> getAllMedicines();

    public List<Medicine> getMedicinesWithLowStock();
}
