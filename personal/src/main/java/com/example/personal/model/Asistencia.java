package com.example.personal.model;

import com.example.personal.util.Rut;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asistencias")
@Data
@NoArgsConstructor
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String rut;
    @JsonIgnore
    @Column(nullable = false)
    private String rutLimpio;
    @Column(nullable = false)
    private String fecha;
    @Column(nullable = false)
    private String hora;
    @Column(nullable = false)
    private String tipo; 

    @PrePersist
    @PreUpdate
    void normalizar() {
        rutLimpio = Rut.limpiar(rut);
    }
}