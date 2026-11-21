package com.example.usuarios.dto;

import com.example.usuarios.model.Usuario;

public final class Dtos {
    private Dtos() {}

    public record LoginRequest(String correo, String rut, String password) {}

    public record RegistroRequest(String nombre, String empresa, String rut, String correo,
                                  String telefono, String password) {}

    public record UsuarioRequest(String nombre, String rut, String correo, String telefono,
                                 String password, String rol, String empresa) {}

    public record UsuarioDto(Long id, String nombre, String rut, String correo, String telefono,
                             String rol, String empresa) {
        public static UsuarioDto de(Usuario u) {
            return new UsuarioDto(u.getId(), u.getNombre(), u.getRut(), u.getCorreo(),
                    u.getTelefono(), u.getRol(), u.getEmpresa());
        }
    }

    public record AuthResponse(String token, UsuarioDto usuario) {}
}