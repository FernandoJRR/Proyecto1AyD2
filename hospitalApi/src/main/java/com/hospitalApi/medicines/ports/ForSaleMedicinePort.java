package com.hospitalApi.medicines.ports;

import java.util.List;

import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForSaleMedicinePort {

    public SaleMedicine findById(String id) throws NotFoundException;

    public SaleMedicine createSaleMedicine(String medicineId, Integer quantity) throws NotFoundException;

    public SaleMedicine createSaleMedicine(String consultId, String medicineId, Integer quantity)
            throws NotFoundException, UnsupportedOperationException;

    public List<SaleMedicine> getSalesMedicinesByConsultId(String consultId)
            throws NotFoundException, UnsupportedOperationException;

    public List<SaleMedicine> getSalesMedicinesByMedicineId(String medicineId) throws NotFoundException;

    public List<SaleMedicine> getSalesMedicineBetweenDates(String startDate, String endDate);

    public Double totalSalesMedicinesBetweenDates(String startDate, String endDate);

    public Double totalSalesMedicinesByConsult(String consultId)
            throws NotFoundException, UnsupportedOperationException;

    public Double totalSalesMedicinesByMedicine(String medicineId) throws NotFoundException;

    public List<SaleMedicine> getAllSalesMedicines();

}
