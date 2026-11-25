package com.example.usuarios.controller;

import com.example.usuarios.dto.Dtos.*;
import com.example.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "CRUD de cuentas (solo Administrador)")
public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Lista los usuarios")
    @GetMapping
    public List<UsuarioDto> listar() {
        return service.listar();
    }

    @Operation(summary = "Obtiene un usuario por id")
    @GetMapping("/{id}")
    public UsuarioDto obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @Operation(summary = "Crea un usuario con cualquier rol")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDto crear(@RequestBody UsuarioRequest d) {
        return service.crear(d);
    }

    @Operation(summary = "Actualiza un usuario (clave vacia = no cambia)")
    @PutMapping("/{id}")
    public UsuarioDto actualizar(@PathVariable Long id, @RequestBody UsuarioRequest d) {
        return service.actualizar(id, d);
    }

    @Operation(summary = "Elimina un usuario")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id,
                         @RequestHeader(value = "X-User-Id", required = false) Long solicitante) {
        service.eliminar(id, solicitante);
    }
}