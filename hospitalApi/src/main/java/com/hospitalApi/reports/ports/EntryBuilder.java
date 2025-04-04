package com.hospitalApi.reports.ports;

import java.util.List;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.reports.dtos.response.financialReport.FinancialReportEntryDTO;
import com.hospitalApi.reports.enums.FinancialReportType;
import com.hospitalApi.rooms.models.RoomUsage;
import com.hospitalApi.surgery.models.Surgery;

public interface EntryBuilder {

    /**
     * Construye entradas de reporte financiero a partir de ventas de medicamentos,
     * incluyendo nombre del medicamento, precio, costo y cantidad vendida.
     *
     * @param elements lista de ventas de medicamentos realizadas.
     * @param type     tipo de reporte financiero a generar.
     * @return lista de entradas detalladas para el reporte financiero.
     */
    public List<FinancialReportEntryDTO> buildResponseFromSales(List<SaleMedicine> elements,
            FinancialReportType type);

    /**
     * Construye entradas de reporte financiero a partir de consultas médicas
     * pagadas,
     * utilizando el costo de consulta y la información del paciente.
     *
     * @param elements lista de consultas médicas realizadas.
     * @param type     tipo de reporte financiero a generar.
     * @return lista de entradas detalladas para el reporte financiero.
     */
    public List<FinancialReportEntryDTO> buildResponseFromConsults(List<Consult> elements, FinancialReportType type);

    /**
     * Construye entradas de reporte financiero a partir del uso de habitaciones,
     * considerando días de uso, precio diario y costo de mantenimiento.
     *
     * @param elements lista de usos de habitaciones registrados.
     * @param type     tipo de reporte financiero a generar.
     * @return lista de entradas detalladas para el reporte financiero.
     */
    public List<FinancialReportEntryDTO> buildResponseFromRooms(List<RoomUsage> elements, FinancialReportType type);

    /**
     * Construye entradas de reporte financiero a partir de cirugías realizadas,
     * incluyendo tipo de cirugía, paciente, costo y precio total.
     *
     * @param elements lista de cirugías realizadas.
     * @param type     tipo de reporte financiero a generar.
     * @return lista de entradas detalladas para el reporte financiero.
     */
    public List<FinancialReportEntryDTO> buildResponseFromSurgeries(List<Surgery> elements, FinancialReportType type);
}
