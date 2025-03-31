package com.hospitalApi.medicines.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.hospitalApi.medicines.dtos.MedicineResponseDTO;
import com.hospitalApi.medicines.models.Medicine;

@Mapper(componentModel = "spring")
public interface MedicineMapper {
    public MedicineResponseDTO fromMedicineToMedicineResponseDto(Medicine medicine);
    public List<MedicineResponseDTO> fromMedicineListToMedicineResponseDTOList(List<Medicine> medicines);
}
