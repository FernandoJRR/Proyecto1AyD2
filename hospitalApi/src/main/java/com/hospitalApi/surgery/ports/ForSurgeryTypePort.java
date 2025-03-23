package com.hospitalApi.surgery.ports;

import java.util.List;

import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.dtos.UpdateSurgeryTypeRequestDTO;
import com.hospitalApi.surgery.models.SurgeryType;

public interface ForSurgeryTypePort {
    public List<SurgeryType> getSurgeryTypes(String search);

    public SurgeryType getSurgeryType(String surgeryTypeId) throws NotFoundException;

    public SurgeryType createSurgeryType(SurgeryType surgeryType) throws DuplicatedEntryException;

    public SurgeryType updateSurgeryType(UpdateSurgeryTypeRequestDTO updateSurgeryTypeRequestDTO, String surgeryTypeId)
            throws DuplicatedEntryException, NotFoundException;
}
