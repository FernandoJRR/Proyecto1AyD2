package com.hospitalApi.surgery.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.port.ForConsultPort;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.dtos.DeleteSurgeryResponseDTO;
import com.hospitalApi.surgery.models.Surgery;
import com.hospitalApi.surgery.models.SurgeryType;
import com.hospitalApi.surgery.ports.ForSurgeryPort;
import com.hospitalApi.surgery.ports.ForSurgeryTypePort;
import com.hospitalApi.surgery.repositories.SurgeryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurgeryServices implements ForSurgeryPort {

    private final ForConsultPort forConsultPort;
    private final ForSurgeryTypePort forSurgeryTypePort;
    private final SurgeryRepository surgeryRepository;

    @Override
    public List<Surgery> getSurgerys() {
        return surgeryRepository.findAll();
    }

    @Override
    public Surgery getSurgery(String surgeryId) throws NotFoundException {
        return surgeryRepository.findById(surgeryId)
                .orElseThrow(() -> new NotFoundException("No se encontró la cirugía con id " + surgeryId));
    }

    @Override
    public Surgery createSurgery(String consultId, String surgeryTypeId)
            throws NotFoundException, IllegalStateException {
        Consult consult = forConsultPort.findConsultAndIsNotPaid(consultId);
        SurgeryType surgeryType = forSurgeryTypePort.getSurgeryType(surgeryTypeId);
        Surgery surgery = new Surgery(
                consult,
                surgeryType,
                surgeryType.getHospitalCost(),
                surgeryType.getSurgeryCost());
        surgeryRepository.save(surgery);
        return surgery;
    }

    @Override
    public DeleteSurgeryResponseDTO deleteSurgery(String surgeryId) throws NotFoundException {
        if (!surgeryRepository.existsById(surgeryId)) {
            throw new NotFoundException("No se encontró la cirugía con id " + surgeryId);
        }
        if (surgeryRepository.existsByPerformedDateIsNotNullAndId(surgeryId)) {
            throw new IllegalStateException("No se puede eliminar la cirugía porque ya ha sido realizada.");
        }
        surgeryRepository.deleteById(surgeryId);
        return new DeleteSurgeryResponseDTO(surgeryId, "La cirugía ha sido eliminada correctamente.", true);
    }

    @Override
    public List<Surgery> getSurgerysByConsultId(String consultId) throws NotFoundException {
        forConsultPort.findById(consultId);
        List<Surgery> surgerys = surgeryRepository.findByConsultId(consultId);
        return surgerys;
    }

    @Override
    public Surgery markSurgeryAsPerformed(String surgeryId) throws NotFoundException, IllegalStateException {
        Surgery surgery = getSurgery(surgeryId);
        if (surgery.getPerformedDate() != null) {
            throw new IllegalStateException("La cirugía ya ha sido realizada.");
        }
        surgery.setPerformedDate(java.time.LocalDate.now());
        surgeryRepository.save(surgery);
        return surgery;
    }

    @Override
    public boolean surgeryAsPerformed(String surgeryId) throws NotFoundException {
        Surgery surgery = getSurgery(surgeryId);
        return surgery.getPerformedDate() != null;
    }
}
