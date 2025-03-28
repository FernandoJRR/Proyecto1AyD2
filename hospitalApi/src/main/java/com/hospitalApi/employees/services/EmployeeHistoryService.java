package com.hospitalApi.employees.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hospitalApi.employees.enums.HistoryTypeEnum;
import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeHistory;
import com.hospitalApi.employees.models.EmployeePeriod;
import com.hospitalApi.employees.models.HistoryType;
import com.hospitalApi.employees.ports.ForEmployeeHistoryPort;
import com.hospitalApi.employees.ports.ForHistoryTypePort;
import com.hospitalApi.employees.repositories.EmployeeHistoryRepository;
import com.hospitalApi.shared.exceptions.InvalidPeriodException;
import com.hospitalApi.shared.exceptions.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeHistoryService implements ForEmployeeHistoryPort {

    private final EmployeeHistoryRepository employeeHistoryRepository;
    private final ForHistoryTypePort forHistoryTypePort;

    public EmployeeHistory createEmployeeHistoryHiring(Employee employee, LocalDate hiringDate)
            throws NotFoundException {
        HistoryType historyTypeContratacion = forHistoryTypePort
                .findHistoryTypeByName(HistoryTypeEnum.CONTRATACION.getType());

        EmployeeHistory employeeHistory = new EmployeeHistory("Se realizo la contratacion con un salario de Q."+employee.getSalary());

        employeeHistory.setHistoryType(historyTypeContratacion);
        employeeHistory.setEmployee(employee);
        employeeHistory.setHistoryDate(hiringDate);

        return employeeHistoryRepository.save(employeeHistory);
    }

    public EmployeeHistory createEmployeeHistorySalaryIncrease(Employee employee, BigDecimal newSalary,
            LocalDate increaseDate)
            throws NotFoundException, InvalidPeriodException {

        if (!isValidEmployeePeriod(employee, increaseDate)) {
            throw new InvalidPeriodException("El incremento se realiza en un periodo invalido");
        }

        HistoryType historyTypeContratacion = forHistoryTypePort
                .findHistoryTypeByName(HistoryTypeEnum.AUMENTO_SALARIAL.getType());

        EmployeeHistory employeeHistory = new EmployeeHistory(newSalary.toString());

        employeeHistory.setHistoryType(historyTypeContratacion);
        employeeHistory.setEmployee(employee);
        employeeHistory.setHistoryDate(increaseDate);

        return employeeHistoryRepository.save(employeeHistory);
    }

    public EmployeeHistory createEmployeeHistorySalaryDecrease(Employee employee, BigDecimal newSalary,
            LocalDate decreaseDate)
            throws NotFoundException, InvalidPeriodException {

        if (!isValidEmployeePeriod(employee, decreaseDate)) {
            throw new InvalidPeriodException("El decremento se realiza en un periodo invalido");
        }

        HistoryType historyTypeContratacion = forHistoryTypePort
                .findHistoryTypeByName(HistoryTypeEnum.DISMINUCION_SALARIAL.getType());

        EmployeeHistory employeeHistory = new EmployeeHistory(newSalary.toString());

        employeeHistory.setHistoryType(historyTypeContratacion);
        employeeHistory.setEmployee(employee);
        employeeHistory.setHistoryDate(decreaseDate);

        return employeeHistoryRepository.save(employeeHistory);
    }

    public EmployeeHistory createEmployeeHistoryDeactivation(Employee employee, LocalDate deactivationDate, HistoryType historyTypeReason)
            throws NotFoundException, InvalidPeriodException {

        if (!isValidEmployeePeriodDeactivationDate(employee, deactivationDate)) {
            throw new InvalidPeriodException("La desactivacion no esta en un periodo valido.");
        }

        HistoryType historyTypeDeactivation = forHistoryTypePort
                .findHistoryTypeById(historyTypeReason.getId());

        EmployeeHistory employeeHistory = new EmployeeHistory("El empleado se ha desactivado por "+historyTypeDeactivation.getType());

        employeeHistory.setHistoryType(historyTypeDeactivation);
        employeeHistory.setEmployee(employee);
        employeeHistory.setHistoryDate(deactivationDate);

        return employeeHistoryRepository.save(employeeHistory);
    }

    public EmployeeHistory createEmployeeHistoryReactivation(Employee employee, LocalDate reactivationDate)
            throws NotFoundException, InvalidPeriodException {

        if (!isValidEmployeePeriodReactivationDate(employee, reactivationDate)) {
            throw new InvalidPeriodException("La desactivacion no esta en un periodo valido.");
        }

        HistoryType historyTypeDeactivation = forHistoryTypePort
                .findHistoryTypeByName(HistoryTypeEnum.RECONTRATACION.getType());

        EmployeeHistory employeeHistory = new EmployeeHistory("El empleado se ha recontratado.");

        employeeHistory.setHistoryType(historyTypeDeactivation);
        employeeHistory.setEmployee(employee);
        employeeHistory.setHistoryDate(reactivationDate);

        return employeeHistoryRepository.save(employeeHistory);
    }

    public List<EmployeeHistory> getEmployeeHistory(Employee employee) throws NotFoundException {
        return employeeHistoryRepository.findAllByEmployee_IdOrderByHistoryDateAsc(employee.getId());
    }

    public Optional<EmployeeHistory> getLastEmployeeSalaryUntilDate(Employee employee, LocalDate date)
            throws NotFoundException {
        HistoryType aumentoHistoryType = this.forHistoryTypePort
                .findHistoryTypeByName(HistoryTypeEnum.AUMENTO_SALARIAL.getType());
        HistoryType disminucionHistoryType = this.forHistoryTypePort
                .findHistoryTypeByName(HistoryTypeEnum.DISMINUCION_SALARIAL.getType());
        List<String> salaryHistoryTypes = Arrays.asList(aumentoHistoryType.getId(), disminucionHistoryType.getId());
        return employeeHistoryRepository
                .findFirstByEmployee_IdAndHistoryType_IdInAndHistoryDateLessThanEqualOrderByHistoryDateDesc(
                        employee.getId(), salaryHistoryTypes, date);
    }

    public Optional<EmployeeHistory> getMostRecentEmployeeSalary(Employee employee) throws NotFoundException {
        HistoryType aumentoHistoryType = this.forHistoryTypePort
                .findHistoryTypeByName(HistoryTypeEnum.AUMENTO_SALARIAL.getType());
        HistoryType disminucionHistoryType = this.forHistoryTypePort
                .findHistoryTypeByName(HistoryTypeEnum.DISMINUCION_SALARIAL.getType());
        List<String> salaryHistoryTypes = Arrays.asList(aumentoHistoryType.getId(), disminucionHistoryType.getId());
        return employeeHistoryRepository
                .findFirstByEmployee_IdAndHistoryType_IdInOrderByHistoryDateDesc(employee.getId(), salaryHistoryTypes);
    }

    public Optional<EmployeeHistory> getEmployeeHiringDate(Employee employee) throws NotFoundException {
        return employeeHistoryRepository.findFirstByEmployee_IdOrderByHistoryDateAsc(employee.getId());
    }

    public boolean isValidEmployeePeriod(Employee employee, LocalDate date) {
        List<String> startTypes = Arrays.asList(HistoryTypeEnum.CONTRATACION.getType(),
                HistoryTypeEnum.RECONTRATACION.getType());
        List<String> endTypes = Arrays.asList(HistoryTypeEnum.DESPIDO.getType(),
                HistoryTypeEnum.RENUNCIA.getType());

        List<String> validTypes = new ArrayList<>();
        validTypes.addAll(startTypes);
        validTypes.addAll(endTypes);

        // se obtienen los registros del empleado en orden
        List<EmployeeHistory> registers = employeeHistoryRepository
                .findByEmployee_IdAndHistoryType_TypeInOrderByHistoryDateAsc(employee.getId(), validTypes);

        // se crea una lista con todos los periodos en los que el empleado trabajo en el
        // hospital
        List<EmployeePeriod> periods = new ArrayList<>();
        EmployeePeriod currentPeriod = null;
        for (EmployeeHistory register : registers) {
            String type = register.getHistoryType().getType();
            LocalDate eventDate = register.getHistoryDate();

            if (startTypes.contains(type)) {
                // un periodo empieza con una contratacion o recontratacion
                currentPeriod = new EmployeePeriod(eventDate);
                periods.add(currentPeriod);
            } else if (endTypes.contains(type)) {
                // un periodo termina con una renuncia o despido
                if (currentPeriod != null && currentPeriod.getEnd() == null) {
                    currentPeriod.setEnd(eventDate);

                    currentPeriod = null;
                }
            }
        }


        // se itera sobre todos los periodos para ver si la fecha entra en alguno
        for (EmployeePeriod period : periods) {
            // si no tiene fecha de fin el periodo es el actual
            if (period.getEnd() == null) {
                if (!date.isBefore(period.getStart())) {
                    // si la fecha es mayor al periodo actual es valida
                    return true;
                }
            } else if (!date.isBefore(period.getStart()) && !date.isAfter(period.getEnd())) {
                // si la fecha entra en un periodo pasado es valida
                return true;
            }
        }

        return false;
    }

    public boolean isValidEmployeePeriodDeactivationDate(Employee employee, LocalDate deactivationDate) {
        List<String> startTypes = Arrays.asList(HistoryTypeEnum.CONTRATACION.getType(),
                HistoryTypeEnum.RECONTRATACION.getType());
        List<String> endTypes = Arrays.asList(HistoryTypeEnum.DESPIDO.getType(),
                HistoryTypeEnum.RENUNCIA.getType());

        List<String> validTypes = new ArrayList<>();
        validTypes.addAll(startTypes);
        validTypes.addAll(endTypes);

        // se obtiene la ultima desactivacion del cliente y su fecha de contratacion
        Optional<EmployeeHistory> hiringDateHistoryOptional = employeeHistoryRepository
            .findFirstByEmployee_IdOrderByHistoryDateAsc(employee.getId());
        Optional<EmployeeHistory> lastDeactivationHistoryOptional = employeeHistoryRepository
                .findFirstByEmployee_IdAndHistoryType_TypeInOrderByHistoryDateDesc(employee.getId(), endTypes);

        // en caso de que se quiera desactivar en una fecha previa a la contratacion el periodo es invalido
        if (hiringDateHistoryOptional.get().getHistoryDate().isAfter(deactivationDate)) {
            return false;
        }

        if (lastDeactivationHistoryOptional.isPresent()) {
            // si la fecha a desactivar es antes que la ultima desactivacion
            if (lastDeactivationHistoryOptional.get().getHistoryDate().isAfter(deactivationDate)) {
                return false;
            }
        }

        return true;
    }

    public boolean isValidEmployeePeriodReactivationDate(Employee employee, LocalDate reactivationDate) {
        List<String> startTypes = Arrays.asList(HistoryTypeEnum.CONTRATACION.getType(),
                HistoryTypeEnum.RECONTRATACION.getType());
        List<String> endTypes = Arrays.asList(HistoryTypeEnum.DESPIDO.getType(),
                HistoryTypeEnum.RENUNCIA.getType());

        List<String> validTypes = new ArrayList<>();
        validTypes.addAll(startTypes);
        validTypes.addAll(endTypes);

        // se obtiene la ultima desactivacion del cliente y su fecha de contratacion
        Optional<EmployeeHistory> hiringDateHistoryOptional = employeeHistoryRepository
            .findFirstByEmployee_IdOrderByHistoryDateAsc(employee.getId());
        Optional<EmployeeHistory> lastDeactivationHistoryOptional = employeeHistoryRepository
                .findFirstByEmployee_IdAndHistoryType_TypeInOrderByHistoryDateDesc(employee.getId(), endTypes);

        // en caso de que se quiera reactivar en una fecha previa a la contratacion el periodo es invalido
        if (hiringDateHistoryOptional.get().getHistoryDate().isAfter(reactivationDate)) {
            return false;
        }

        // si la fecha a desactivar es antes que la ultima recontratacion
        if (lastDeactivationHistoryOptional.get().getHistoryDate().isAfter(reactivationDate)) {
            return false;
        }

        return true;
    }
}
