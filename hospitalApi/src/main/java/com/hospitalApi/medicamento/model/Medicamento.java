package com.hospitalApi.medicamento.model;

import com.hospitalApi.medicamento.DTO.MedicamentoRequestDTO;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "medicamento")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(name = "descripcion")
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @Column(name = "cantidad")
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser menor a 0")
    private Integer cantidad;

    @Column(name = "cantidad_minima")
    @NotNull(message = "La cantidad mínima es obligatoria")
    @Min(value = 0, message = "La cantidad mínima no puede ser menor a 0")
    private Integer cantidadMinima;

    @Column(name = "precio")
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1, message = "El precio no puede ser menor a Q1.00")
    private Double precio;

    public Medicamento() {
    }

    public Medicamento(Long id, String nombre, String descripcion, Integer cantidad, Double precio,
            Integer cantidadMinima) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.cantidadMinima = cantidadMinima;
    }

    public Medicamento update(MedicamentoRequestDTO medicamentoRequestDTO) {
        // Por cada atributo que sea diferente de null, se actualiza el valor del
        // atributo
        if (medicamentoRequestDTO.getNombre() != null) {
            this.nombre = medicamentoRequestDTO.getNombre();
        }
        if (medicamentoRequestDTO.getDescripcion() != null) {
            this.descripcion = medicamentoRequestDTO.getDescripcion();
        }
        if (medicamentoRequestDTO.getCantidad() != null) {
            this.cantidad = medicamentoRequestDTO.getCantidad();
        }
        if (medicamentoRequestDTO.getPrecio() != null) {
            this.precio = medicamentoRequestDTO.getPrecio();
        }
        if (medicamentoRequestDTO.getCantidadMinima() != null) {
            this.cantidadMinima = medicamentoRequestDTO.getCantidadMinima();
        }
        return this;
    }

    @Override
    public String toString() {
        return "Medicamento [cantidad=" + cantidad + ", cantidadMinima=" + cantidadMinima + ", descripcion=" + descripcion
                + ", id=" + id + ", nombre=" + nombre + ", precio=" + precio + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Medicamento other = (Medicamento) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
