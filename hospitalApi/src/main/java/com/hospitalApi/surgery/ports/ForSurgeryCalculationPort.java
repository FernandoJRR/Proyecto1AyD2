package com.hospitalApi.surgery.ports;

public interface ForSurgeryCalculationPort {
    public Double totalSurgerisByConsult(String consultId) throws IllegalStateException;
    public Boolean allSurgeriesPerformedByConsultId(String consultId);
    public Boolean consultHaveSugeriesPerformed(String consultId); 
}
