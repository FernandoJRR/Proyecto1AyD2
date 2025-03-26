package com.hospitalApi.medicines.mappers;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;

import com.hospitalApi.medicines.dtos.MedicineResponseDTO;
import com.hospitalApi.medicines.dtos.SaleMedicineDTO;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.models.SaleMedicine;

@Mapper(componentModel = "spring")
public interface MedicineMapper {
    public MedicineResponseDTO toMedicineResponseDTO(Medicine medicine);

    public List<MedicineResponseDTO> fromMedicineListToMedicineResponseDTOList(List<Medicine> medicines);

    default List<SaleMedicineDTO> fromSaleMedicineListToSaleMedicineDTOList(List<SaleMedicine> saleMedicines) {
        if (saleMedicines == null) {
            return null;
        }
        List<SaleMedicineDTO> saleMedicineDTOs = new ArrayList<SaleMedicineDTO>();
        for (SaleMedicine saleMedicine : saleMedicines) {
            saleMedicineDTOs.add(new SaleMedicineDTO(saleMedicine));
        }
        return saleMedicineDTOs;
    }
}
