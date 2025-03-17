package com.hospitalApi.medicines.mappers;

import org.mapstruct.Mapper;

import com.hospitalApi.medicines.dtos.MedicineResponseDTO;
import com.hospitalApi.medicines.models.Medicine;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedicineMapper {
    public MedicineResponseDTO toMedicineResponseDTO(Medicine medicine);
    public List<MedicineResponseDTO> fromMedicineListToMedicineResponseDTOList(List<Medicine> medicines);
}
