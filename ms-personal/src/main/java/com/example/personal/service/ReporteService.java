package com.example.personal.service;

import com.example.personal.dto.Dtos.*;
import com.example.personal.model.Asistencia;
import com.example.personal.repository.AsistenciaRepository;
import com.example.personal.repository.TrabajadorRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReporteService {
    private final AsistenciaRepository asistencias;
    private final TrabajadorRepository trabajadores;

    public ReporteService(AsistenciaRepository asistencias, TrabajadorRepository trabajadores) {
        this.asistencias = asistencias;
        this.trabajadores = trabajadores;
    }

    // desde y hasta son opcionales (formato AAAA-MM-DD)
    public ReporteHoras horas(String desde, String hasta) {
        List<Asistencia> marcas = asistencias.findAll().stream()
                .filter(a -> desde == null || a.getFecha().compareTo(desde) >= 0)
                .filter(a -> hasta == null || a.getFecha().compareTo(hasta) <= 0)
                .toList();

        Map<String, List<Asistencia>> porDia = marcas.stream()
                .collect(Collectors.groupingBy(a -> a.getRutLimpio() + "|" + a.getFecha()));

        Map<String, Double> horasPorRut = new HashMap<>();
        porDia.forEach((clave, lista) -> {
            String rut = clave.substring(0, clave.indexOf('|'));
            Optional<String> entrada = lista.stream().filter(a -> "Entrada".equals(a.getTipo()))
                    .map(Asistencia::getHora).min(String::compareTo);
            Optional<String> salida = lista.stream().filter(a -> "Salida".equals(a.getTipo()))
                    .map(Asistencia::getHora).max(String::compareTo);
            if (entrada.isEmpty() || salida.isEmpty()) return;
            double horas = (minutos(salida.get()) - minutos(entrada.get())) / 60.0;
            if (horas > 0) horasPorRut.merge(rut, horas, Double::sum);
        });

        List<FilaReporte> filas = trabajadores.findAll().stream()
                .filter(t -> horasPorRut.getOrDefault(t.getRutLimpio(), 0.0) > 0)
                .map(t -> {
                    double h = horasPorRut.get(t.getRutLimpio());
                    return new FilaReporte(t.getId(), t.getRut(), t.getNombre(), t.getEspecialidad(),
                            t.getCliente(), t.getValorHora(), redondear(h), Math.round(h * t.getValorHora()));
                })
                .toList();

        Map<String, List<FilaReporte>> porCliente = filas.stream()
                .collect(Collectors.groupingBy(FilaReporte::cliente, LinkedHashMap::new, Collectors.toList()));
        List<TotalCliente> totales = porCliente.entrySet().stream()
                .map(e -> new TotalCliente(e.getKey(),
                        redondear(e.getValue().stream().mapToDouble(FilaReporte::horas).sum()),
                        e.getValue().stream().mapToLong(FilaReporte::costo).sum()))
                .toList();

        return new ReporteHoras(filas, totales);
    }

    private static int minutos(String hhmm) {
        return Integer.parseInt(hhmm.substring(0, 2)) * 60 + Integer.parseInt(hhmm.substring(3, 5));
    }

    private static double redondear(double v) {
        return Math.round(v * 100) / 100.0;
    }
}