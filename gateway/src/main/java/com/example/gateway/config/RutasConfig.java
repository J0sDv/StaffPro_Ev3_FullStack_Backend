package com.example.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RutasConfig {

    @Bean
    public RouteLocator rutas(RouteLocatorBuilder b,
                              @Value("${destino.usuarios}") String usuarios,
                              @Value("${destino.personal}") String personal,
                              @Value("${destino.solicitudes}") String solicitudes) {
        return b.routes()
                .route("ms-usuarios", r -> r.path("/auth/**", "/usuarios/**").uri(usuarios))
                .route("ms-personal", r -> r.path("/trabajadores/**", "/asistencias/**", "/reportes/**").uri(personal))
                .route("ms-solicitudes", r -> r.path("/solicitudes/**").uri(solicitudes))
                .build();
    }
}