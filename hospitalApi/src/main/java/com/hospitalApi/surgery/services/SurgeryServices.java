package com.hospitalApi.surgery.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.port.ForConsultPort;
import com.hospitalApi.shared.exceptions.NotFoundException;
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
    public Surgery createSurgery(String consultId, String surgeryTypeId) throws NotFoundException {
        Consult consult = forConsultPort.findById(consultId);
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
    public boolean deleteSurgery(String surgeryId) throws NotFoundException {
        if (!surgeryRepository.existsById(surgeryId)) {
            throw new NotFoundException("No se encontró la cirugía con id " + surgeryId);
        }
        surgeryRepository.deleteById(surgeryId);
        return true;
    }

    @Override
    public List<Surgery> getSurgerysByConsultId(String consultId) throws NotFoundException {
        forConsultPort.findById(consultId);
        List<Surgery> surgerys = surgeryRepository.findByConsultId(consultId);
        return surgerys;
    }
}
