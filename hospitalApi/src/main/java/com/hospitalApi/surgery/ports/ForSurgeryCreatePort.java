package com.hospitalApi.surgery.ports;

import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.dtos.CreateSugeryRequestDTO;
import com.hospitalApi.surgery.models.Surgery;

public interface ForSurgeryCreatePort {
    public Surgery createSurgery(CreateSugeryRequestDTO createSugeryRequestDTO)
            throws IllegalStateException, NotFoundException, DuplicatedEntryException;

}
