package com.hospitalApi.consults.port;

import java.util.List;

import com.hospitalApi.consults.dtos.ConsutlFilterDTO;
import com.hospitalApi.consults.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForConsultPort {

	public Consult findById(String id) throws NotFoundException;

	public Consult findConsultAndIsNotPaid(String id) throws NotFoundException, IllegalStateException;

	public Consult createConsult(String patientId, String employeeId, Double costoConsulta)
			throws NotFoundException;

	public Consult updateConsult(String id, UpdateConsultRequestDTO updateConsultRequestDTO)
			throws NotFoundException, IllegalStateException;

	public Double obtenerTotalConsulta(String id) throws NotFoundException;

	public Consult pagarConsulta(String id) throws NotFoundException, IllegalStateException;

	public Consult markConsultInternado(String id, String habitacionId)
			throws NotFoundException, IllegalStateException, DuplicatedEntryException;

	public Consult endInternado(String id) throws NotFoundException, IllegalStateException;

	public Consult endConsult(String id) throws NotFoundException, IllegalStateException;

	public List<Consult> getAllConsults();

	public List<Consult> getConsults(ConsutlFilterDTO consutlFilterDTO);
}