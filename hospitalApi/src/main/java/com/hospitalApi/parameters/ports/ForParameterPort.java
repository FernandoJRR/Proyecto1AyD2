package com.hospitalApi.parameters.ports;

import com.hospitalApi.parameters.models.Parameter;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForParameterPort {

    public Parameter findParameterByKey(String key) throws NotFoundException;

}
