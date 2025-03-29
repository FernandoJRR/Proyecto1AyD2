package com.hospitalApi.surgery.services;

import org.springframework.stereotype.Service;

import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.dtos.CreateSugeryRequestDTO;
import com.hospitalApi.surgery.models.Surgery;
import com.hospitalApi.surgery.ports.ForSurgeryCreatePort;
import com.hospitalApi.surgery.ports.ForSurgeryEmployeePort;
import com.hospitalApi.surgery.ports.ForSurgeryPort;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurgeryCreateService implements ForSurgeryCreatePort {

	private final ForSurgeryPort forSurgeryPort;
	private final ForSurgeryEmployeePort forSurgeryEmployeePort;

	@Override
	@Transactional(rollbackOn = Exception.class)
	public Surgery createSurgery(CreateSugeryRequestDTO createSugeryRequestDTO)
			throws IllegalStateException, NotFoundException, DuplicatedEntryException {
		Surgery surgery = forSurgeryPort.createSurgery(
				createSugeryRequestDTO.getConsultId(),
				createSugeryRequestDTO.getSurgeryTypeId());
		forSurgeryEmployeePort.addDoctorToSurgery(
				surgery.getId(),
				createSugeryRequestDTO.getAsignedDoctorId(), createSugeryRequestDTO.getIsSpecialist());
		surgery = forSurgeryPort.getSurgery(surgery.getId());
		return surgery;
	}

}
