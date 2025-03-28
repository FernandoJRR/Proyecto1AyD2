package com.hospitalApi.medicines.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.port.ForConsultPort;
import com.hospitalApi.medicines.dtos.CreateSaleMedicineConsultRequestDTO;
import com.hospitalApi.medicines.dtos.CreateSaleMedicineFarmaciaRequestDTO;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.medicines.ports.ForMedicinePort;
import com.hospitalApi.medicines.ports.ForSaleMedicinePort;
import com.hospitalApi.medicines.repositories.SaleMedicineRepository;
import com.hospitalApi.shared.exceptions.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaleMedicineService implements ForSaleMedicinePort {

    private final SaleMedicineRepository saleMedicineRepository;
    private final ForMedicinePort forMedicinePort;
    private final ForConsultPort forConsultPort;

    @Override
    public SaleMedicine findById(String id) throws NotFoundException {
        return saleMedicineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venta de medicamento con id " + id + " no encontrada"));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public SaleMedicine createSaleMedicine(String medicineId, Integer quantity) throws NotFoundException {
        // Obtenemos la medicina en base al id
        Medicine medicine = forMedicinePort.getMedicine(medicineId);
        if (medicine == null) {
            throw new NotFoundException("Medicamento con id " + medicineId + " no encontrado");
        }
        // Verificamos si hay suficiente stock
        if (medicine.getQuantity() < quantity) {
            throw new NotFoundException("No hay suficiente stock para el medicamento con id " + medicineId);
        }
        // Creamos una nueva instancia de SaleMedicine
        SaleMedicine newSaleMedicine = new SaleMedicine(medicine, quantity);
        // Guardamos la nueva venta de medicamento en la base de datos
        SaleMedicine saleMedicine = saleMedicineRepository.save(newSaleMedicine);
        // Actualizamos el stock de la medicina
        forMedicinePort.subtractStockMedicine(medicineId, quantity);
        return saleMedicine;
    }

    @Override
    public SaleMedicine createSaleMedicine(String consultId, String medicineId, Integer quantity)
            throws NotFoundException {
        Consult consult = forConsultPort.findById(consultId);
        // Obtenemos la medicina en base al id
        Medicine medicine = forMedicinePort.getMedicine(medicineId);
        // Verificamos si hay suficiente stock
        if (medicine.getQuantity() < quantity) {
            throw new NotFoundException("No hay suficiente stock para el medicamento con id " + medicineId);
        }
        // Creamos una nueva instancia de SaleMedicine
        SaleMedicine newSaleMedicine = new SaleMedicine(consult, medicine, quantity);
        // Guardamos la nueva venta de medicamento en la base de datos
        SaleMedicine saleMedicine = saleMedicineRepository.save(newSaleMedicine);
        // Actualizamos el stock de la medicina
        forMedicinePort.subtractStockMedicine(medicineId, quantity);
        return saleMedicine;
    }

    @Override
    public List<SaleMedicine> getSalesMedicinesByConsultId(String consultId)
            throws NotFoundException {
        forConsultPort.findById(consultId);
        return saleMedicineRepository.findByConsultId(consultId);
    }

    @Override
    public List<SaleMedicine> getSalesMedicinesByMedicineId(String medicineId) throws NotFoundException {
        // Verificamos si el medicamento existe
        Medicine medicine = forMedicinePort.getMedicine(medicineId);
        if (medicine == null) {
            throw new NotFoundException("Medicamento con id " + medicineId + " no encontrado");
        }
        // Obtenemos las ventas de medicina en base al id de la medicina
        return saleMedicineRepository.findByMedicineId(medicineId);
    }

    @Override
    public List<SaleMedicine> getSalesMedicineBetweenDates(String startDate, String endDate) {
        // Convertir las fechas a formato Date
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        // Obtener las ventas de medicina entre las fechas
        return saleMedicineRepository.findByCreatedAtBetween(start, end);
    }

    @Override
    public Double totalSalesMedicinesBetweenDates(String startDate, String endDate) {
        // Convertir las fechas a formato Date
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        // Obtener el total de ventas de medicina entre las fechas
        return saleMedicineRepository.totalSalesMedicinesBetweenDates(start, end);
    }

    @Override
    public Double totalSalesMedicinesByConsult(String consultId)
            throws NotFoundException {
        forConsultPort.findById(consultId);
        // Obtenemos el total de ventas de medicina en base al id de la consulta
        return saleMedicineRepository.totalSalesMedicinesByConsult(consultId);
    }

    @Override
    public Double totalSalesMedicinesByMedicine(String medicineId) throws NotFoundException {
        // Verificamos si el medicamento existe
        Medicine medicine = forMedicinePort.getMedicine(medicineId);
        if (medicine == null) {
            throw new NotFoundException("Medicamento con id " + medicineId + " no encontrado");
        }
        // Obtenemos el total de ventas de medicina en base al id de la medicina
        return saleMedicineRepository.totalSalesMedicinesByMedicine(medicineId);
    }

    @Override
    public List<SaleMedicine> getAllSalesMedicines() {
        return saleMedicineRepository.findAll();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<SaleMedicine> createSaleMedicines(
            List<CreateSaleMedicineFarmaciaRequestDTO> createSaleMedicineFarmaciaRequestDTOs) throws NotFoundException {
        List<SaleMedicine> saleMedicines = new ArrayList<>();
        for (CreateSaleMedicineFarmaciaRequestDTO createSaleMedicineFarmaciaRequestDTO : createSaleMedicineFarmaciaRequestDTOs) {
            SaleMedicine saleMedicine = this.createSaleMedicine(createSaleMedicineFarmaciaRequestDTO.getMedicineId(),
                    createSaleMedicineFarmaciaRequestDTO.getQuantity());
            saleMedicines.add(saleMedicine);
        }
        return saleMedicines;
    }

    @Override
    public List<SaleMedicine> createSaleMedicinesForConsult(
            List<CreateSaleMedicineConsultRequestDTO> createSaleMedicineConsultRequestDTOs) throws NotFoundException {
        List<SaleMedicine> saleMedicines = new ArrayList<>();
        for (CreateSaleMedicineConsultRequestDTO createSaleMedicineConsultRequestDTO : createSaleMedicineConsultRequestDTOs) {
            SaleMedicine saleMedicine = this.createSaleMedicine(createSaleMedicineConsultRequestDTO.getConsultId(),
                    createSaleMedicineConsultRequestDTO.getMedicineId(),
                    createSaleMedicineConsultRequestDTO.getQuantity());
            saleMedicines.add(saleMedicine);
        }
        return saleMedicines;
    }

}
