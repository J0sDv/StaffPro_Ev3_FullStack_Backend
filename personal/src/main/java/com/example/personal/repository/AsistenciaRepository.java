package com.example.personal.repository;

import com.example.personal.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByRutLimpioOrderByFechaDescHoraDesc(String rutLimpio);
    List<Asistencia> findByRutLimpioAndFecha(String rutLimpio, String fecha);
}