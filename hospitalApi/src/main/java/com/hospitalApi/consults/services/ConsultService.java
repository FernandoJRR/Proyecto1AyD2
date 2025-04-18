package com.hospitalApi.consults.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.hospitalApi.consults.port.ForEmployeeConsultPort;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hospitalApi.consults.dtos.ConsutlFilterDTO;
import com.hospitalApi.consults.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.port.ForConsultPort;
import com.hospitalApi.consults.repositories.ConsultRepository;
import com.hospitalApi.consults.specifications.ConsultSpecifications;
import com.hospitalApi.medicines.ports.ForSaleMedicineCalculationPort;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.patients.ports.ForPatientPort;
import com.hospitalApi.rooms.models.RoomUsage;
import com.hospitalApi.rooms.ports.ForRoomUsagePort;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.ports.ForSurgeryCalculationPort;
import org.springframework.data.domain.Sort;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class ConsultService implements ForConsultPort {

    private final ConsultRepository consultRepository;
    private final ForPatientPort forPatientPort;
    private final ForSaleMedicineCalculationPort forSaleMedicineCalculationPort;
    private final ForSurgeryCalculationPort forSurgeryCalculationService;
    private final ForEmployeeConsultPort forEmployeeConsultPort;
    private final ForRoomUsagePort forRoomUsagePort;

    @Override
    public Consult findById(String id) throws NotFoundException {
        return consultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta con id " + id + " no encontrada"));
    }

    /**
     * Manda a traer todas las consultas pagadas dentro del rango de fechas
     * indicado.
     * Si una fecha es nula, no se aplica ese límite (el repo se encarga).
     *
     * @param startDate fecha de inicio del rango, puede ser nula.
     * @param endDate   fecha de fin del rangoF, puede ser nula.
     * @return lista de consultas pagadas en el rango de fechas.
     */
    @Override
    public List<Consult> findPaidConsultsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return consultRepository.findPaidConsultsByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public Consult findConsultAndIsNotPaid(String id) throws NotFoundException, IllegalStateException {
        Consult consult = findById(id);
        // Verificar si la consulta ya fue pagada
        if (consult.getIsPaid()) {
            throw new IllegalStateException("La consulta con id " + id + " ya fue pagada");
        }
        return consult;
    }

    @Override
    public Consult createConsult(String patientId, String employeeId, BigDecimal costoConsulta)
            throws NotFoundException {
        // Creamos la consulta con el paciente
        Patient patient = forPatientPort.getPatient(patientId);
        Consult consult = new Consult(patient, costoConsulta);
        Consult savedConsult = consultRepository.save(consult);
        // Asociamos la consulta con el empleado
        forEmployeeConsultPort.createEmployeeConsult(savedConsult, employeeId);
        return savedConsult;
    }

    @Override
    public Consult updateConsult(String id, UpdateConsultRequestDTO updateConsultRequestDTO)
            throws NotFoundException, IllegalStateException {
        Consult consult = findById(id);
        // Verificar si la consulta ya fue pagada
        if (consult.getIsPaid()) {
            throw new IllegalStateException("La consulta con id " + id + " ya fue pagada no se puede modificar");
        }
        consult.updateConsultFromDTO(updateConsultRequestDTO);
        return consultRepository.save(consult);
    }

    @Override
    public BigDecimal obtenerTotalConsulta(String id) throws NotFoundException, IllegalStateException {
        Consult consult = findById(id);
        Double totalCirugias = forSurgeryCalculationService.totalSurgerisByConsult(id);
        Double totalHabitacion = 0.00;
        if (consult.getIsInternado()) {
            RoomUsage roomUsage = forRoomUsagePort.calcRoomUsage(consult);
            totalHabitacion = roomUsage.getDailyRoomPrice().multiply(BigDecimal.valueOf(roomUsage.getUsageDays())).doubleValue();
        }
        Double totalMedicamentos = forSaleMedicineCalculationPort.totalSalesMedicinesByConsult(id);
        BigDecimal newTotalCost = consult.getCostoConsulta()
                .add(BigDecimal.valueOf(totalCirugias + totalHabitacion + totalMedicamentos));
        consult.setCostoTotal(newTotalCost);
        consultRepository.save(consult);
        return consult.getCostoTotal();
    }

    @Override
    public Consult pagarConsulta(String id) throws NotFoundException, IllegalStateException {
        Consult consult = findById(id);
        // Verificar si las cirugías han sido realizadas
        if (!forSurgeryCalculationService.allSurgeriesPerformedByConsultId(id)) {
            throw new IllegalStateException(
                    "No se puede pagar la consulta porque no todas las cirugías han sido realizadas.");
        }
        // Verificar si la consulta ya fue pagada
        if (consult.getIsPaid()) {
            throw new IllegalStateException("La consulta con id " + id + " ya fue pagada");
        }
        // Cerrar el uso de la habitación si es que existe
        if (consult.getIsInternado()) {
            forRoomUsagePort.closeRoomUsage(consult);
        }
        consult.setIsPaid(true);
        return consultRepository.save(consult);
    }

    @Override
    public List<Consult> getAllConsults() {
        return consultRepository.findAll();
    }

    @Override
    public List<Consult> getConsults(ConsutlFilterDTO consutlFilterDTO) {
        Specification<Consult> patientSpecification = Specification
                .where(ConsultSpecifications.hasPatientDpi(consutlFilterDTO.getPatientDpi()))
                .or(ConsultSpecifications.hasPatientFirstnames(consutlFilterDTO.getPatientFirstnames()))
                .or(ConsultSpecifications.hasPatientLastnames(consutlFilterDTO.getPatientLastnames()));

        Specification<Consult> employeeSpecification = Specification
                .where(ConsultSpecifications.hasEmployeeId(consutlFilterDTO.getEmployeeId()))
                .or(ConsultSpecifications.hasEmployeeFirstName(consutlFilterDTO.getEmployeeFirstName()))
                .or(ConsultSpecifications.hasEmployeeLastName(consutlFilterDTO.getEmployeeLastName()));

        Specification<Consult> specification = Specification
                .where(ConsultSpecifications.hasId(consutlFilterDTO.getConsultId()))
                .and(patientSpecification)
                .and(employeeSpecification)
                .and(ConsultSpecifications.isPaid(consutlFilterDTO.getIsPaid()))
                .and(ConsultSpecifications.isInternado(consutlFilterDTO.getIsInternado()));

        List<Consult> consults = consultRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt"));
        return consults;
    }

    @Override
    public Consult markConsultInternado(String id, String habitacionId)
            throws NotFoundException, IllegalStateException, DuplicatedEntryException {
        Consult consult = findConsultAndIsNotPaid(id);
        forRoomUsagePort.asignRoomToConsult(habitacionId, consult);
        consult.setIsInternado(true);
        consult = consultRepository.save(consult);
        return consult;
    }

    @Override
    public boolean deleteConsult(String id) throws NotFoundException, IllegalStateException {
        // Se busca la consulta por id y si esta pagada se lanza una excepcion
        Consult consult = findConsultAndIsNotPaid(id);
        // Verificamos si la consulta tiene cirugias asociadas realizadas
        Boolean hasPerformedSurgeries = forSurgeryCalculationService.consultHaveSugeriesPerformed(consult.getId());
        if (hasPerformedSurgeries) {
            throw new IllegalStateException("No se puede eliminar la consulta porque tiene cirugías realizadas.");
        }
        // Verificamos si la consulta tiene medicamentos asociados
        Boolean hasMedicines = forSaleMedicineCalculationPort.consultHaveMedicines(consult.getId());
        if (hasMedicines) {
            throw new IllegalStateException("No se puede eliminar la consulta porque tiene medicamentos asociados.");
        }
        return true;
    }
}
