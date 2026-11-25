package com.example.gateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final SecretKey key;

    public JwtAuthFilter(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();
        String metodo = req.getMethod().name();
        String ruta = req.getURI().getPath();

        // Nunca confiar en cabeceras internas que vengan del cliente
        ServerHttpRequest.Builder limpia = req.mutate().headers(h -> {
            h.remove("X-User-Id");
            h.remove("X-User-Rol");
            h.remove("X-User-Rut");
            h.remove("X-User-Correo");
        });

        // Rutas publicas: login y registro
        if (AccessRules.esPublica(metodo, ruta)) {
            return chain.filter(exchange.mutate().request(limpia.build()).build());
        }

        // Verificar el token (firma y vencimiento)
        String auth = req.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            return rechazar(exchange, HttpStatus.UNAUTHORIZED, "Falta el token de autenticacion.");
        }
        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(auth.substring(7)).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return rechazar(exchange, HttpStatus.UNAUTHORIZED, "Token invalido o expirado.");
        }

        // Verificar que el rol tenga permiso para esta ruta y metodo
        String rol = claims.get("rol", String.class);
        if (!AccessRules.permitido(metodo, ruta, rol)) {
            return rechazar(exchange, HttpStatus.FORBIDDEN, "Tu rol no tiene permiso para esta accion.");
        }

        // Pasar la identidad a los microservicios
        ServerHttpRequest conUsuario = limpia
                .header("X-User-Id", claims.getSubject())
                .header("X-User-Rol", rol)
                .header("X-User-Rut", claims.get("rut", String.class))
                .header("X-User-Correo", claims.get("correo", String.class))
                .build();
        return chain.filter(exchange.mutate().request(conUsuario).build());
    }

    private Mono<Void> rechazar(ServerWebExchange exchange, HttpStatus estado, String mensaje) {
        exchange.getResponse().setStatusCode(estado);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] cuerpo;
        try {
            cuerpo = MAPPER.writeValueAsBytes(Map.of("error", mensaje));
        } catch (JsonProcessingException e) {
            cuerpo = new byte[0];
        }
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(cuerpo)));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}