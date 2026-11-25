package com.example.solicitudes.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "solicitudes")
@Data
@NoArgsConstructor
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long usuarioId;     
    @Column(nullable = false)
    private String empresa;
    @Column(nullable = false)
    private String contacto;
    @Column(nullable = false)
    private String correo;
    @Column(nullable = false)
    private String telefono;
    @Column(nullable = false)
    private String tipoContrato;   
    @Column(nullable = false)
    private String especialidad;
    @Column(nullable = false)
    private Integer cantidad;
    @Column(nullable = false)
    private String fechaInicio;
    @Column(nullable = false)
    private String fechaTermino;
    @Column(length = 500)
    private String comentarios;
    @Column(nullable = false)
    private String estado;         
    @Column(nullable = false)
    private String fechaCreacion;
}