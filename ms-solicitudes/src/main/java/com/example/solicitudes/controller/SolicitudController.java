package com.example.solicitudes.controller;

import com.example.solicitudes.dto.Dtos.*;
import com.example.solicitudes.model.Solicitud;
import com.example.solicitudes.service.SolicitudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/solicitudes")
@Tag(name = "Solicitudes", description = "Solicitudes de personal de los clientes")
public class SolicitudController {
    private final SolicitudService service;

    public SolicitudController(SolicitudService service) {
        this.service = service;
    }

    @Operation(summary = "Lista solicitudes (el Cliente solo ve las suyas). Las cabeceras X-User-* las agrega el Gateway")
    @GetMapping
    public List<Solicitud> listar(@RequestHeader(value = "X-User-Rol", required = false) String rol,
                                  @RequestHeader(value = "X-User-Id", required = false) Long usuarioId) {
        return service.listar(rol, usuarioId);
    }

    @Operation(summary = "Obtiene una solicitud por id")
    @GetMapping("/{id}")
    public Solicitud obtener(@PathVariable Long id,
                             @RequestHeader(value = "X-User-Rol", required = false) String rol,
                             @RequestHeader(value = "X-User-Id", required = false) Long usuarioId) {
        return service.obtener(id, rol, usuarioId);
    }

    @Operation(summary = "Crea una solicitud (409 si no hay personal disponible suficiente)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Solicitud crear(@RequestBody SolicitudRequest d,
                           @RequestHeader(value = "X-User-Id", required = false) Long usuarioId) {
        return service.crear(d, usuarioId);
    }

    @Operation(summary = "Cambia el estado de una solicitud")
    @PutMapping("/{id}/estado")
    public Solicitud cambiarEstado(@PathVariable Long id, @RequestBody EstadoRequest d) {
        return service.cambiarEstado(id, d.estado());
    }
}