package com.hospitalApi.medicines.ports;

public interface ForSaleMedicineCalculationPort {
    public Double totalSalesMedicinesByConsult(String consultId);
    public Boolean consultHaveMedicines(String consultId);
}
