package com.example.personal.dto;

import java.util.List;

public final class Dtos {
    private Dtos() {}

    public record TrabajadorRequest(String nombre, String rut, String especialidad,
                                    Integer valorHora, String cliente, String estado) {}

    public record AsignacionRequest(String cliente) {}

    public record MarcajeRequest(String rut, String fecha, String hora, String tipo) {}

    public record FilaReporte(Long id, String rut, String nombre, String especialidad,
                              String cliente, Integer valorHora, double horas, long costo) {}

    public record TotalCliente(String cliente, double horas, long costo) {}

    public record ReporteHoras(List<FilaReporte> filas, List<TotalCliente> totales) {}
}