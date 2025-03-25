package com.hospitalApi.surgery.ports;

import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForSurgeryCalculationPort {
    public Double totalSurgerisByConsult(String consultId);
}
