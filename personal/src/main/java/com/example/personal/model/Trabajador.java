package com.example.personal.model;

import com.example.personal.util.Rut;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trabajadores")
@Data
@NoArgsConstructor
public class Trabajador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String rut;
    @JsonIgnore
    @Column(nullable = false, unique = true)
    private String rutLimpio;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String especialidad;
    @Column(nullable = false)
    private Integer valorHora;
    @Column(nullable = false)
    private String cliente;
    @Column(nullable = false)
    private String estado; 

    @PrePersist
    @PreUpdate
    void normalizar() {
        rutLimpio = Rut.limpiar(rut);
    }
}