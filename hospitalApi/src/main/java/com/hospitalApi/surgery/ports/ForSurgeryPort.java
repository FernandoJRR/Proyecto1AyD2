package com.hospitalApi.surgery.ports;

import java.util.List;

import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.dtos.DeleteSurgeryResponseDTO;
import com.hospitalApi.surgery.models.Surgery;

public interface ForSurgeryPort {

    public List<Surgery> getSurgerys();

    public Surgery getSurgery(String surgeryId) throws NotFoundException;

    public Surgery createSurgery(String consultId, String surgeryTypeId)
            throws NotFoundException, IllegalStateException;

    public DeleteSurgeryResponseDTO deleteSurgery(String surgeryId) throws NotFoundException;

    public List<Surgery> getSurgerysByConsultId(String consultId) throws NotFoundException;

    public Surgery markSurgeryAsPerformed(String surgeryId) throws NotFoundException, IllegalStateException;

    public boolean surgeryAsPerformed(String surgeryId) throws NotFoundException;
}
