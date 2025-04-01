package com.hospitalApi.medicines.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hospitalApi.medicines.dtos.SaleMedicineResponseDTO;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.shared.utils.DateFormatterUtil;

@Mapper(componentModel = "spring", uses = { DateFormatterUtil.class })
public interface SaleMedicineMapper {

    // esta onda deja el id de la consulta en null
    @Mapping(target = "consultId", expression = "java(saleMedicine.getConsult() != null ? saleMedicine.getConsult().getId() : null)")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "formatDateToLocalFormat")
    public SaleMedicineResponseDTO fromMedicineSaleToSaleMedicineDTO(SaleMedicine saleMedicine);

    public List<SaleMedicineResponseDTO> fromSaleMedicineListToSaleMedicineDTOList(List<SaleMedicine> saleMedicines);
}
