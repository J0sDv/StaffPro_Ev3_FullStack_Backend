package com.example.personal.service;

import com.example.personal.dto.Dtos.*;
import com.example.personal.exception.ApiException;
import com.example.personal.model.Asistencia;
import com.example.personal.repository.AsistenciaRepository;
import com.example.personal.repository.TrabajadorRepository;
import com.example.personal.util.Rut;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class AsistenciaService {
    private static final ZoneId CHILE = ZoneId.of("America/Santiago");
    private static final String TRABAJADOR = "Trabajador";

    private final AsistenciaRepository asistencias;
    private final TrabajadorRepository trabajadores;

    public AsistenciaService(AsistenciaRepository asistencias, TrabajadorRepository trabajadores) {
        this.asistencias = asistencias;
        this.trabajadores = trabajadores;
    }

    // Un Trabajador solo ve sus propios marcajes, los demas roles ven todos
    public List<Asistencia> listar(String rol, String rutSesion) {
        if (TRABAJADOR.equals(rol) && rutSesion != null) {
            return asistencias.findByRutLimpioOrderByFechaDescHoraDesc(Rut.limpiar(rutSesion));
        }
        return asistencias.findAll(Sort.by(Sort.Direction.DESC, "fecha", "hora"));
    }

    public Asistencia marcar(MarcajeRequest d, String rol, String rutSesion) {
        // Un Trabajador solo puede marcar a su nombre, aunque envie otro RUT
        String rut = (TRABAJADOR.equals(rol) && rutSesion != null) ? rutSesion : d.rut();

        List<String> errores = new ArrayList<>();
        if (!Rut.valido(rut)) errores.add("El RUT no es valido.");
        if (!"Entrada".equals(d.tipo()) && !"Salida".equals(d.tipo())) errores.add("El tipo debe ser Entrada o Salida.");
        if (d.hora() == null || !d.hora().matches("([01][0-9]|2[0-3]):[0-5][0-9]")) {
            errores.add("La hora debe tener formato HH:mm.");
        }
        LocalDate fecha = null;
        try {
            fecha = LocalDate.parse(d.fecha());
        } catch (RuntimeException e) {
            errores.add("La fecha debe tener formato AAAA-MM-DD.");
        }
        if (fecha != null && fecha.isAfter(LocalDate.now(CHILE))) errores.add("No puedes marcar en una fecha futura.");
        if (!errores.isEmpty()) throw new ApiException(HttpStatus.BAD_REQUEST, String.join(" ", errores));

        String limpio = Rut.limpiar(rut);
        if (trabajadores.findByRutLimpio(limpio).isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No existe un trabajador con ese RUT.");
        }
        // Regla: para marcar salida debe haber una entrada anterior ese mismo dia
        if ("Salida".equals(d.tipo())) {
            boolean hayEntrada = asistencias.findByRutLimpioAndFecha(limpio, d.fecha()).stream()
                    .anyMatch(a -> "Entrada".equals(a.getTipo()) && a.getHora().compareTo(d.hora()) < 0);
            if (!hayEntrada) {
                throw new ApiException(HttpStatus.CONFLICT, "Para marcar salida necesitas una entrada previa ese dia.");
            }
        }
        Asistencia a = new Asistencia();
        a.setRut(rut.trim());
        a.setFecha(d.fecha());
        a.setHora(d.hora());
        a.setTipo(d.tipo());
        return asistencias.save(a);
    }
}