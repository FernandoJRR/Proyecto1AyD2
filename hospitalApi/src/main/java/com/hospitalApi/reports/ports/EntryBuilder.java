package com.hospitalApi.reports.ports;

import java.util.List;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.reports.dtos.response.financialReport.FinancialReportEntryDTO;
import com.hospitalApi.reports.enums.FinancialReportType;
import com.hospitalApi.rooms.models.RoomUsage;
import com.hospitalApi.surgery.models.Surgery;

public interface EntryBuilder {
    public List<FinancialReportEntryDTO> buildResponseFromSales(List<SaleMedicine> elements,
            FinancialReportType type);

    public List<FinancialReportEntryDTO> buildResponseFromConsults(List<Consult> elements, FinancialReportType type);

    public List<FinancialReportEntryDTO> buildResponseFromRooms(List<RoomUsage> elements, FinancialReportType type);

    public List<FinancialReportEntryDTO> buildResponseFromSurgeries(List<Surgery> elements, FinancialReportType type);
}
