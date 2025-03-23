package com.hospitalApi.surgery.ports;

import java.util.List;

import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.models.Surgery;

public interface ForSurgeryPort {

    public List<Surgery> getSurgerys();

    public Surgery getSurgery(String surgeryId) throws NotFoundException;

    public Surgery createSurgery(String consultId, String surgeryTypeId) throws NotFoundException;

    public boolean deleteSurgery(String surgeryId) throws NotFoundException;

    public Double totalSurgerisByConsult(String consultId) throws NotFoundException;
}
