package com.hospitalApi.medicines.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hospitalApi.medicines.dtos.SaleMedicineResponseDTO;
import com.hospitalApi.medicines.models.SaleMedicine;

@Mapper(componentModel = "spring")
public interface SaleMedicineMapper {

    // esta onda deja el id de la consulta en null
    @Mapping(target = "consultId", expression = "java(saleMedicine.getConsult() != null ? saleMedicine.getConsult().getId() : null)")
    public SaleMedicineResponseDTO fromMedicineSaleToSaleMedicineDTO(SaleMedicine saleMedicine);

    public List<SaleMedicineResponseDTO> fromSaleMedicineListToSaleMedicineDTOList(List<SaleMedicine> saleMedicines);
}
