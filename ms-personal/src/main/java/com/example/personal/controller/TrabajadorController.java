package com.example.personal.controller;

import com.example.personal.dto.Dtos.*;
import com.example.personal.model.Trabajador;
import com.example.personal.service.TrabajadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trabajadores")
@Tag(name = "Trabajadores", description = "CRUD de trabajadores y asignacion a clientes")
public class TrabajadorController {
    private final TrabajadorService service;

    public TrabajadorController(TrabajadorService service) {
        this.service = service;
    }

    @Operation(summary = "Lista los trabajadores")
    @GetMapping
    public List<Trabajador> listar() {
        return service.listar();
    }

    @Operation(summary = "Cuenta los trabajadores Disponibles de una especialidad (lo usa ms-solicitudes)")
    @GetMapping("/disponibles")
    public Map<String, Object> disponibles(@RequestParam String especialidad) {
        return service.disponibles(especialidad);
    }

    @Operation(summary = "Cuenta los trabajadores asignados a un cliente")
    @GetMapping("/asignados")
    public Map<String, Object> asignados(@RequestParam String cliente) {
        return service.asignados(cliente);
    }

    @Operation(summary = "Ficha del trabajador que inicia sesion (usa X-User-Rut del Gateway)")
    @GetMapping("/mi-ficha")
    public Trabajador miFicha(@RequestHeader(value = "X-User-Rut", required = false) String rut) {
        return service.miFicha(rut);
    }

    @Operation(summary = "Obtiene un trabajador por id")
    @GetMapping("/{id}")
    public Trabajador obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @Operation(summary = "Crea un trabajador")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Trabajador crear(@RequestBody TrabajadorRequest d) {
        return service.crear(d);
    }

    @Operation(summary = "Actualiza un trabajador")
    @PutMapping("/{id}")
    public Trabajador actualizar(@PathVariable Long id, @RequestBody TrabajadorRequest d) {
        return service.actualizar(id, d);
    }

    @Operation(summary = "Asigna un trabajador Disponible a un cliente (pasa a En turno)")
    @PutMapping("/{id}/asignacion")
    public Trabajador asignar(@PathVariable Long id, @RequestBody AsignacionRequest d) {
        return service.asignar(id, d.cliente());
    }

    @Operation(summary = "Elimina un trabajador")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}