package com.hospitalApi.medicamento.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicamentoRequestDTO {
    private String nombre;
    private String descripcion;
    private Integer cantidad;
    private Integer cantidadMinima;
    private Double precio;

    public MedicamentoRequestDTO() {
    }

    public MedicamentoRequestDTO(String nombre, String descripcion, Integer cantidad, Integer cantidadMinima,
            Double precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.cantidadMinima = cantidadMinima;
        this.precio = precio;
    }

}
