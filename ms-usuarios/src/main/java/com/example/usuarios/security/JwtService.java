package com.example.usuarios.security;

import com.example.usuarios.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long minutos;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiracion-minutos}") long minutos) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.minutos = minutos;
    }

    public String generar(Usuario u) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(u.getId()))
                .claim("rol", u.getRol())
                .claim("rut", u.getRut())
                .claim("correo", u.getCorreo())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(minutos, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }
}