package com.hospitalApi.employees.ports;

import com.hospitalApi.employees.models.HistoryType;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForHistoryTypePort {
    public HistoryType findHistoryTypeByName(String historyTypeName) throws NotFoundException;
}
