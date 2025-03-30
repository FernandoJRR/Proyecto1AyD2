package com.hospitalApi.medicines.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.medicines.dtos.CreateMedicineRequestDTO;
import com.hospitalApi.medicines.dtos.UpdateMedicineRequestDTO;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.ports.ForMedicinePort;
import com.hospitalApi.medicines.repositories.MedicineRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class MedicineService implements ForMedicinePort {

    private final MedicineRepository medicineRepository;

    @Override
    public Medicine createMedicine(CreateMedicineRequestDTO createMedicineRequestDTO) throws DuplicatedEntryException {
        // Verificamos si el nombre del medicamento ya existe
        if (medicineRepository.existsByName(createMedicineRequestDTO.getName())) {
            throw new DuplicatedEntryException(
                    "El medicamento con nombre " + createMedicineRequestDTO.getName() + " ya existe");
        }
        // Generamos una nueva instancia de Medicine en base a los datos del DTO
        Medicine newMedicine = new Medicine(createMedicineRequestDTO);
        // Guardamos el nuevo medicamento en la base de datos
        return medicineRepository.save(newMedicine);
    }

    @Override
    public Medicine updateMedicine(String id, UpdateMedicineRequestDTO updateMedicineRequestDTO)
            throws DuplicatedEntryException, NotFoundException {
        // Obtenemos la medicina en base el id
        Medicine currentMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Medicamento con id " + id + " no encontrado"));
        // Puede que la actualizacion contenga el mismo nombre que el actual medicamento
        // si es
        // diferente verificamos si el nombre ya existe
        if (!currentMedicine.getName().equals(updateMedicineRequestDTO.getName())) {
            // Verificamos si el nombre del medicamento ya existe
            if (medicineRepository.existsByName(updateMedicineRequestDTO.getName())) {
                throw new DuplicatedEntryException(
                        "El medicamento con nombre " + updateMedicineRequestDTO.getName() + " ya existe");
            }
        }
        // Actualizamos los datos de la medicina
        currentMedicine = currentMedicine.updateFromDTO(updateMedicineRequestDTO);
        // Guardamos los cambios en la base de datos
        return medicineRepository.save(currentMedicine);
    }

    @Override
    public Medicine getMedicine(String id) throws NotFoundException {
        return medicineRepository.findById(id).orElseThrow(() -> new NotFoundException("Medicamento no encontrado"));
    }

    @Override
    public List<Medicine> getAllMedicines(String query) {
        if (query != null) {
            return medicineRepository.findByNameContainingIgnoreCase(query);
        }
        return medicineRepository.findAll();
    }

    @Override
    public List<Medicine> getMedicinesWithLowStock() {
        return medicineRepository.findMedicinesWithLowStock();
    }

    @Override
    public Medicine updateStockMedicine(String id, Integer quantity) throws NotFoundException {
        Medicine currentMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Medicamento con id " + id + " no encontrado"));
        currentMedicine.setQuantity(quantity);
        return medicineRepository.save(currentMedicine);
    }

    @Override
    public Medicine sumStockMedicine(String id, Integer quantity) throws NotFoundException {
        Medicine currentMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Medicamento con id " + id + " no encontrado"));
        currentMedicine.setQuantity(currentMedicine.getQuantity() + quantity);
        return medicineRepository.save(currentMedicine);
    }

    @Override
    public Medicine subtractStockMedicine(String id, Integer quantity) throws NotFoundException {
        Medicine currentMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Medicamento con id " + id + " no encontrado"));
        currentMedicine.setQuantity(currentMedicine.getQuantity() - quantity);
        return medicineRepository.save(currentMedicine);
    }

}
