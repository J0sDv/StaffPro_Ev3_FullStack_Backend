package com.example.solicitudes.dto;

public final class Dtos {
    private Dtos() {}

    public record SolicitudRequest(String empresa, String contacto, String correo, String telefono,
                                   String tipoContrato, String especialidad, Integer cantidad,
                                   String fechaInicio, String fechaTermino, String comentarios) {}

    public record EstadoRequest(String estado) {}
}