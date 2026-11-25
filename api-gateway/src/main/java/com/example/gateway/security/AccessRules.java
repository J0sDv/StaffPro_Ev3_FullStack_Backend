package com.example.gateway.security;

import org.springframework.util.AntPathMatcher;

import java.util.List;

public final class AccessRules {
    public static final String ADMIN = "Administrador";
    public static final String RRHH = "Jefe de RR.HH.";
    public static final String COORD = "Coordinador";
    public static final String RECLUTA = "Reclutador";
    public static final String CLIENTE = "Cliente";
    public static final String TRABAJADOR = "Trabajador";

    // roles == null -> ruta publica | roles vacio -> cualquier usuario con token | lista -> solo esos roles
    private record Regla(String metodo, String patron, List<String> roles) {}

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    // El primer patron que coincide gana. Lo que no aparece aqui se bloquea.
    private static final List<Regla> REGLAS = List.of(
            new Regla("POST", "/auth/login", null),
            new Regla("POST", "/auth/registro", null),
            new Regla("POST", "/auth/refresh", List.of()),

            new Regla("*", "/usuarios/**", List.of(ADMIN)),

            new Regla("PUT", "/trabajadores/*/asignacion", List.of(COORD, ADMIN)),
            new Regla("GET", "/trabajadores/asignados", List.of()),
            new Regla("GET", "/trabajadores/mi-ficha", List.of(TRABAJADOR)),
            new Regla("GET", "/trabajadores/**", List.of(ADMIN, RRHH, COORD, RECLUTA)),
            new Regla("*", "/trabajadores/**", List.of(RECLUTA, ADMIN)),

            new Regla("POST", "/asistencias", List.of(TRABAJADOR)),
            new Regla("GET", "/asistencias", List.of(TRABAJADOR, RRHH, ADMIN)),
            new Regla("GET", "/reportes/**", List.of(RRHH, ADMIN)),

            new Regla("PUT", "/solicitudes/*/estado", List.of(COORD, ADMIN)),
            new Regla("POST", "/solicitudes", List.of(CLIENTE)),
            new Regla("GET", "/solicitudes/**", List.of(CLIENTE, COORD, RRHH, ADMIN))
    );

    private AccessRules() {}

    public static boolean esPublica(String metodo, String ruta) {
        Regla r = buscar(metodo, ruta);
        return r != null && r.roles() == null;
    }

    public static boolean permitido(String metodo, String ruta, String rol) {
        Regla r = buscar(metodo, ruta);
        if (r == null) return false;
        return r.roles() == null || r.roles().isEmpty() || r.roles().contains(rol);
    }

    private static Regla buscar(String metodo, String ruta) {
        for (Regla r : REGLAS) {
            boolean metodoOk = "*".equals(r.metodo()) || r.metodo().equals(metodo);
            if (metodoOk && MATCHER.match(r.patron(), ruta)) return r;
        }
        return null;
    }
}
