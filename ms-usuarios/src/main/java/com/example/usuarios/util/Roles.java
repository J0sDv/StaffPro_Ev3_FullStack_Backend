package com.example.usuarios.util;

import java.util.List;

public final class Roles {
    public static final String ADMIN = "Administrador";
    public static final String RRHH = "Jefe de RR.HH.";
    public static final String COORDINADOR = "Coordinador";
    public static final String RECLUTADOR = "Reclutador";
    public static final String CLIENTE = "Cliente";
    public static final String TRABAJADOR = "Trabajador";
    public static final List<String> TODOS = List.of(ADMIN, RRHH, COORDINADOR, RECLUTADOR, CLIENTE, TRABAJADOR);

    private Roles() {}
}