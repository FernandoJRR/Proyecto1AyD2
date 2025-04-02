package com.hospitalApi.vacations.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.repositories.EmployeeRepository;
import com.hospitalApi.parameters.enums.ParameterEnum;
import com.hospitalApi.parameters.models.Parameter;
import com.hospitalApi.parameters.repositories.ParameterRepository;
import com.hospitalApi.shared.exceptions.InvalidPeriodException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.vacations.models.Vacations;
import com.hospitalApi.vacations.ports.ForVacationsPort;
import com.hospitalApi.vacations.repositories.VacationsRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class VacationsService implements ForVacationsPort {

    private final VacationsRepository vacationsRepository;
    private final ParameterRepository parameterRepository;
    private final EmployeeRepository employeeRepository;

    public List<Vacations> getAllVacationsForEmployeeOnPeriod(String employeeId, Integer period)
        throws NotFoundException {

            return vacationsRepository.findAllByEmployee_IdAndPeriodYearOrderByBeginDateAsc(employeeId, period);
        }

    public Map<Integer, List<Vacations>> getAllVacationsForEmployee(String employeeId)
        throws NotFoundException {
            List<Vacations> foundVacations =  vacationsRepository.findAllByEmployee_IdOrderByBeginDateAsc(employeeId);

             Map<Integer, List<Vacations>> groupedVacations = foundVacations.stream()
                .collect(Collectors.groupingBy(Vacations::getPeriodYear));

            return groupedVacations;
        }

    public List<Vacations> createVacationsForEmployeeOnPeriod(String employeeId, Integer period, List<Vacations> vacationsPeriods)
        throws NotFoundException, InvalidPeriodException {

        Employee currentEmployee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new NotFoundException("No se ha encontrado al empleado ingresado"));

        if (vacationsRepository.existsByEmployee_IdAndPeriodYear(employeeId, period)) {
            throw new InvalidPeriodException("Ya existen vacaciones en el periodo ingresado");
        }

        if (!areValidVacationPeriods(vacationsPeriods, period)) {
            throw new InvalidPeriodException("Las fechas de las vacaciones no son validas");
        }

        Integer amountVacationDays = totalWorkingDays(vacationsPeriods);
        Optional<Parameter> parameterDays = parameterRepository.findOneByParameterKey(ParameterEnum.DIAS_VACACIONES.getKey());

        if (parameterDays.isEmpty()) {
            throw new NotFoundException("Ocurrio un error al crear las vacaciones");
        }

        Integer daysVacations = Integer.parseInt(parameterDays.get().getValue());
        if (!amountVacationDays.equals(daysVacations)) {
            // si la cantidad de dias laborales que se pusieron en los periodos de vacaciones no son iguales a los dias en el sistema es invalido
            throw new InvalidPeriodException("La cantidad de dias de vacaciones ingresados no son validos");
        }

        // si las validaciones se cumplen entonces se guardan las vacaciones
        for (Vacations vacations : vacationsPeriods) {
            vacations.setWorkingDays(countWorkingDays(vacations.getBeginDate(), vacations.getEndDate()));
            vacations.setWasUsed(false);
            vacations.setEmployee(currentEmployee);
            vacations.setPeriodYear(period);
            vacationsRepository.save(vacations);
        }

        return vacationsRepository.findAllByEmployee_IdAndPeriodYearOrderByBeginDateAsc(employeeId, period);
    }

    public List<Vacations> updateVacationsForEmployeeOnPeriod(String employeeId, Integer period, List<Vacations> vacationsPeriods)
        throws NotFoundException, InvalidPeriodException {


        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);

        if (employeeOptional.isEmpty()) {
            throw new NotFoundException("No se ha encontrado al empleado ingresado");
        }

        if (!vacationsRepository.existsByEmployee_IdAndPeriodYear(employeeId, period)) {
            throw new InvalidPeriodException("No existen vacaciones previas en el periodo ingresado");
        }

        List<Vacations> usedVacationsOnPeriod = vacationsRepository.findAllByEmployee_IdAndPeriodYearAndWasUsedTrue(employeeId, period);

        if (usedVacationsOnPeriod.size() > 0) {
            throw new InvalidPeriodException("Se han usado vacaciones del empleado por lo que no se pueden modificar");
        }

        Employee currentEmployee = employeeOptional.get();

        if (!areValidVacationPeriods(vacationsPeriods, period)) {
            throw new InvalidPeriodException("Las fechas de las vacaciones no son validas");
        }

        Integer amountVacationDays = totalWorkingDays(vacationsPeriods);
        Optional<Parameter> parameterDays = parameterRepository.findOneByParameterKey(ParameterEnum.DIAS_VACACIONES.getKey());

        if (parameterDays.isEmpty()) {
            throw new NotFoundException("Ocurrio un error al crear las vacaciones");
        }

        Integer daysVacations = Integer.parseInt(parameterDays.get().getValue());
        if (!amountVacationDays.equals(daysVacations)) {
            // si la cantidad de dias laborales que se pusieron en los periodos de vacaciones no son iguales a los dias en el sistema es invalido
            throw new InvalidPeriodException("La cantidad de dias de vacaciones ingresados no son validos");
        }

        // si las validaciones se cumplen entonces se eliminan las viejas vacaciones y se guardan las nuevas
        vacationsRepository.deleteByPeriodYear(period);

        for (Vacations vacations : vacationsPeriods) {
            vacations.setWorkingDays(countWorkingDays(vacations.getBeginDate(), vacations.getEndDate()));
            vacations.setWasUsed(false);
            vacations.setEmployee(currentEmployee);
            vacations.setPeriodYear(period);
            vacationsRepository.save(vacations);
        }

        return vacationsRepository.findAllByEmployee_IdAndPeriodYearOrderByBeginDateAsc(employeeId, period);
    }

    public List<Vacations> createRandomVacationsForEmployee(String employeeId) throws NotFoundException {
        Optional<Parameter> parameterDays = parameterRepository.findOneByParameterKey(ParameterEnum.DIAS_VACACIONES.getKey());

        if (parameterDays.isEmpty()) {
            throw new NotFoundException("Ocurrio un error al crear las vacaciones");
        }

        Integer daysVacations = Integer.parseInt(parameterDays.get().getValue());

        int currentPeriodYear = LocalDate.now().getYear();

        // se definen las fechas de diciembre
        LocalDate decemberStart = LocalDate.of(currentPeriodYear, 12, 1);
        LocalDate decemberEnd = LocalDate.of(currentPeriodYear, 12, 31);

        Integer decemberWorkingDays = countWorkingDays(decemberStart, decemberEnd);

        LocalDate beginDate;
        LocalDate endDate;

        if (decemberWorkingDays >= daysVacations) {
            // si la cantidad de dias laborales de diciembre son suficientes se intenta crear el periodo
            beginDate = decemberStart;

            while (!isWorkingDay(beginDate)) {
                beginDate = beginDate.plusDays(1);
            }

            endDate = beginDate;

            int count = 0;

            // se agregan dias al final de la fecha hasta que se llega a la cantidad de dias habiles
            while (count < daysVacations) {
                if (isWorkingDay(endDate)) {
                    count++;
                }
                if (count < daysVacations) {
                    endDate = endDate.plusDays(1);
                }
            }

            // si la fecha fin del periodo termina en enero se crea mejor un periodo en ese mes
            if (endDate.getMonthValue() != 12) {
                return createVacationPeriodForNextYear(employeeId, daysVacations);
            }
        } else {
            // si no hay suficientes dias laborales en diciembre se hace el periodo en enero
            return createVacationPeriodForNextYear(employeeId, daysVacations);
        }

        // si no ocurrieron errores se crea el periodo para diciembre
        Vacations vacation = new Vacations();
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("El empleado ingresado no fue encontrado"));

        vacation.setEmployee(employee);
        vacation.setPeriodYear(currentPeriodYear);
        vacation.setWasUsed(false);
        vacation.setBeginDate(beginDate);
        vacation.setEndDate(endDate);
        vacation.setWorkingDays(countWorkingDays(beginDate, endDate));
        vacationsRepository.save(vacation);
        return Arrays.asList(vacation);
    }

    public List<Vacations> createVacationPeriodForNextYear(String employeeId, Integer daysVacations) throws NotFoundException {
        int nextYear = LocalDate.now().getYear() + 1;
        LocalDate januaryStart = LocalDate.of(nextYear, 1, 1);
        LocalDate beginDate = januaryStart;

        // se encuentra el primer dia laboral de enero
        while (!isWorkingDay(beginDate)) {
            beginDate = beginDate.plusDays(1);
        }

        LocalDate endDate = beginDate;
        int count = 0;
        while (count < daysVacations) {
            if (isWorkingDay(endDate)) {
                count++;
            }
            if (count < daysVacations) {
                endDate = endDate.plusDays(1);
            }
        }
        Vacations vacation = new Vacations();
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        vacation.setEmployee(employee);
        vacation.setPeriodYear(nextYear);
        vacation.setWasUsed(false);
        vacation.setBeginDate(beginDate);
        vacation.setEndDate(endDate);
        vacation.setWorkingDays(countWorkingDays(beginDate, endDate));
        vacationsRepository.save(vacation);
        return Arrays.asList(vacation);
    }

    private boolean areValidVacationPeriods(List<Vacations> vacations, Integer periodYear) throws NotFoundException {
        // primero se valida si todos los Vacations tienen fechas dentro del periodo que se ingreso
        for (Vacations vacation : vacations) {
            if (vacation.getBeginDate().getYear() != periodYear ||
                vacation.getEndDate().getYear() != periodYear) {
                return false;
            }
        }


        // se ordenan por beginDate para hacer las verificaciones
        List<Vacations> sortedVacations = new ArrayList<>(vacations);
        sortedVacations.sort(Comparator.comparing(Vacations::getBeginDate));

        for (int i = 0; i < sortedVacations.size() - 1; i++) {
            Vacations current = sortedVacations.get(i);
            Vacations next = sortedVacations.get(i + 1);
            // si este periodo termina DESPUES que el inicio del siguiente hay un traslape y no es valido
            if (!current.getEndDate().isBefore(next.getBeginDate())) {
                return false;
            }
        }

        return true;
    }

    private Integer totalWorkingDays(List<Vacations> vacations) {
        Integer totalDays = 0;
        // se suman todos los dias habiles de todas las vacaciones
        for (Vacations vacation : vacations) {
            totalDays += countWorkingDays(vacation.getBeginDate(), vacation.getEndDate());
        }
        return totalDays;
    }

    private Integer countWorkingDays(LocalDate beginDate, LocalDate endDate) {
        Integer workingDays = 0;
        // se suman todos los dias habiles de entre dos fechas
        for (LocalDate date = beginDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (isWorkingDay(date)) {
                workingDays++;
            }
        }
        return workingDays;
    }

    private boolean isWorkingDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
    }
}
