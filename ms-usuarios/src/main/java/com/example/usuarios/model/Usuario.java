package com.example.usuarios.model;

import com.example.usuarios.util.Rut;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String rut;
    @Column(nullable = false, unique = true)
    private String rutLimpio;
    @Column(nullable = false, unique = true)
    private String correo;
    private String telefono;
    @Column(nullable = false)
    private String password;   // siempre guardada como hash BCrypt
    @Column(nullable = false)
    private String rol;
    private String empresa;

    @PrePersist
    @PreUpdate
    void normalizar() {
        rutLimpio = Rut.limpiar(rut);
        correo = correo.trim().toLowerCase();
    }
}