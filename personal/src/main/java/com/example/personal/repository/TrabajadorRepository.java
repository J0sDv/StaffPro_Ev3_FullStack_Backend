package com.example.personal.repository;

import com.example.personal.model.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {
    Optional<Trabajador> findByRutLimpio(String rutLimpio);
    List<Trabajador> findByEstadoAndEspecialidad(String estado, String especialidad);
}