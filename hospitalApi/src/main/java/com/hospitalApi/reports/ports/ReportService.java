package com.hospitalApi.reports.ports;

/**
 * Esta interfaz define el contrato para cualquier servicio que genere reportes
 * a partir de un filtro específico. Puede ser implementada por distintos tipos
 * de
 * reportes dentro del sistema.
 *
 * @param R el tipo de resultado del reporte (puede ser una lista, objeto DTO,
 *            etc).
 * @param F el tipo del filtro utilizado para generar el reporte.
 *
 */
public interface ReportService<R, F> {

    /**
     * Genera un reporte basado en el filtro proporcionado.
     *
     * @param filter el filtro con los criterios a aplicar en el reporte.
     * @return el resultado del reporte generado.
     */
    public R generateReport(F filter);
}
