package com.example.personal.controller;

import com.example.personal.dto.Dtos.*;
import com.example.personal.model.Asistencia;
import com.example.personal.service.AsistenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asistencias")
@Tag(name = "Asistencias", description = "Marcajes de entrada y salida")
public class AsistenciaController {
    private final AsistenciaService service;

    public AsistenciaController(AsistenciaService service) {
        this.service = service;
    }

    @Operation(summary = "Lista marcajes (el Trabajador solo ve los suyos). Las cabeceras X-User-* las agrega el Gateway")
    @GetMapping
    public List<Asistencia> listar(@RequestHeader(value = "X-User-Rol", required = false) String rol,
                                   @RequestHeader(value = "X-User-Rut", required = false) String rut) {
        return service.listar(rol, rut);
    }

    @Operation(summary = "Registra una entrada o salida")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Asistencia marcar(@RequestBody MarcajeRequest d,
                             @RequestHeader(value = "X-User-Rol", required = false) String rol,
                             @RequestHeader(value = "X-User-Rut", required = false) String rut) {
        return service.marcar(d, rol, rut);
    }
}