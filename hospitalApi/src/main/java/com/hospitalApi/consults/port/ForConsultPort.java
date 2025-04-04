package com.hospitalApi.consults.port;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.hospitalApi.consults.dtos.ConsutlFilterDTO;
import com.hospitalApi.consults.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForConsultPort {

	public Consult findById(String id) throws NotFoundException;

	public Consult findConsultAndIsNotPaid(String id) throws NotFoundException, IllegalStateException;

	public Consult createConsult(String patientId, String employeeId, BigDecimal costoConsulta)
			throws NotFoundException;

	public Consult updateConsult(String id, UpdateConsultRequestDTO updateConsultRequestDTO)
			throws NotFoundException, IllegalStateException;

	public BigDecimal obtenerTotalConsulta(String id) throws NotFoundException, IllegalStateException;

	public Consult pagarConsulta(String id) throws NotFoundException, IllegalStateException;

	public Consult markConsultInternado(String id, String habitacionId)
			throws NotFoundException, IllegalStateException, DuplicatedEntryException;

	public List<Consult> getAllConsults();

	public List<Consult> getConsults(ConsutlFilterDTO consutlFilterDTO);

	public boolean deleteConsult(String id) throws NotFoundException, IllegalStateException;

	/**
	 * Manda a traer todas las consultas pagadas dentro del rango de fechas
	 * indicado.
	 *
	 * @param startDate fecha de inicio del rango
	 * @param endDate   fecha de fin del rango
	 * @return lista de consultas pagadas en el rango de fechas.
	 */
	public List<Consult> findPaidConsultsBetweenDates(LocalDate startDate, LocalDate endDate);
}