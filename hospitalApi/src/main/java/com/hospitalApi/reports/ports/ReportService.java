package com.hospitalApi.reports.ports;

public interface ReportService<Return,Filters> {

    public Return generateReport(Filters filters);
}
