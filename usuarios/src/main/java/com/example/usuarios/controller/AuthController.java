package com.example.usuarios.controller;

import com.example.usuarios.dto.Dtos.*;
import com.example.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticacion", description = "Login, registro y renovacion de token JWT")
public class AuthController {
    private final UsuarioService service;

    public AuthController(UsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Inicia sesion con correo, RUT y contraseña; devuelve el JWT")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest r) {
        return service.login(r);
    }

    @Operation(summary = "Crea una cuenta de Cliente y devuelve el JWT")
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registro(@RequestBody RegistroRequest r) {
        return service.registro(r);
    }

    @Operation(summary = "Renueva el token (el Gateway valida el token vigente y envia X-User-Id)")
    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestHeader("X-User-Id") Long id) {
        return service.refrescar(id);
    }
}