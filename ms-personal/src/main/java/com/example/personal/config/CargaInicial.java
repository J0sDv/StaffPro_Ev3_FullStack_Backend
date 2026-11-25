package com.example.personal.config;

import com.example.personal.model.Asistencia;
import com.example.personal.model.Trabajador;
import com.example.personal.repository.AsistenciaRepository;
import com.example.personal.repository.TrabajadorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CargaInicial implements CommandLineRunner {
    private final TrabajadorRepository trabajadores;
    private final AsistenciaRepository asistencias;

    public CargaInicial(TrabajadorRepository trabajadores, AsistenciaRepository asistencias) {
        this.trabajadores = trabajadores;
        this.asistencias = asistencias;
    }

    @Override
    public void run(String... args) {
        if (trabajadores.count() == 0) {
            t("11.111.111-1", "Juan Perez", "Operador de grua", 12000, "Construccion SA", "En turno");
            t("22.222.222-2", "Maria Gonzalez", "Bodeguero/a", 8500, "Retail Center", "En turno");
            t("33.333.333-3", "Carlos Silva", "Electricista", 14000, "Sin asignar", "Disponible");
            t("44.444.444-4", "Ana Rojas", "Ayudante de produccion", 6500, "Industrial Sur", "En turno");
            t("55.555.555-5", "Luis Muñoz", "Conductor", 11000, "Sin asignar", "Disponible");
            t("66.666.666-6", "Pedro Soto", "Operador de grua", 12500, "Sin asignar", "Disponible");
            t("77.777.777-7", "Daniela Vega", "Bodeguero/a", 8800, "Sin asignar", "Disponible");
            t("88.888.888-8", "Marcelo Diaz", "Electricista", 13500, "Construccion SA", "En turno");
        }
        if (asistencias.count() == 0) {
            a("11.111.111-1", "2026-10-14", "08:00", "Entrada");
            a("11.111.111-1", "2026-10-14", "17:00", "Salida");
            a("11.111.111-1", "2026-10-15", "08:05", "Entrada");
            a("11.111.111-1", "2026-10-15", "17:10", "Salida");
            a("22.222.222-2", "2026-10-15", "09:00", "Entrada");
            a("22.222.222-2", "2026-10-15", "18:00", "Salida");
            a("44.444.444-4", "2026-10-15", "07:55", "Entrada");
            a("44.444.444-4", "2026-10-15", "16:30", "Salida");
        }
    }

    private void t(String rut, String nombre, String especialidad, int valorHora, String cliente, String estado) {
        Trabajador t = new Trabajador();
        t.setRut(rut);
        t.setNombre(nombre);
        t.setEspecialidad(especialidad);
        t.setValorHora(valorHora);
        t.setCliente(cliente);
        t.setEstado(estado);
        trabajadores.save(t);
    }

    private void a(String rut, String fecha, String hora, String tipo) {
        Asistencia a = new Asistencia();
        a.setRut(rut);
        a.setFecha(fecha);
        a.setHora(hora);
        a.setTipo(tipo);
        asistencias.save(a);
    }
}