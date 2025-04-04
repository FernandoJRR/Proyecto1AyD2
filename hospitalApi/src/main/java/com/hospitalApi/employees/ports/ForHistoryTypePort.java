package com.hospitalApi.employees.ports;

import java.util.List;

import com.hospitalApi.employees.models.HistoryType;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForHistoryTypePort {
    public HistoryType findHistoryTypeByName(String historyTypeName) throws NotFoundException;

    public HistoryType findHistoryTypeById(String historyTypeId) throws NotFoundException;

    public List<HistoryType> findDeactivationHistoryTypes();

    public List<HistoryType> findAll();
}
