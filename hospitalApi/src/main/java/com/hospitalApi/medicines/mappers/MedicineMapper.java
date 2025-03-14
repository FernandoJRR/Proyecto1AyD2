package com.hospitalApi.medicines.mappers;

import org.mapstruct.Mapper;

import com.hospitalApi.medicines.dtos.MedicineResponseDTO;
import com.hospitalApi.medicines.models.Medicine;

@Mapper(componentModel = "spring")
public interface MedicineMapper {
    public MedicineResponseDTO toMedicineResponseDTO(Medicine medicine);
}
