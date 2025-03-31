package com.hospitalApi.reports.ports;

public interface ReportService<R, F> {

    public R generateReport(F filter);
}
