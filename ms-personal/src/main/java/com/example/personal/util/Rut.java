package com.example.personal.util;

public final class Rut {
    private Rut() {}

    public static String limpiar(String rut) {
        return rut == null ? "" : rut.replace(".", "").replace("-", "").trim().toUpperCase();
    }

    public static boolean valido(String rutCompleto) {
        String r = limpiar(rutCompleto);
        if (!r.matches("[0-9]+[0-9K]") || r.length() < 8) return false;
        String cuerpo = r.substring(0, r.length() - 1);
        char dv = r.charAt(r.length() - 1);
        int suma = 0;
        int mult = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += (cuerpo.charAt(i) - '0') * mult;
            mult = (mult == 7) ? 2 : mult + 1;
        }
        int resto = 11 - (suma % 11);
        char esperado = (resto == 11) ? '0' : (resto == 10) ? 'K' : (char) ('0' + resto);
        return dv == esperado;
    }
}