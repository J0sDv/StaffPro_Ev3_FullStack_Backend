package com.example.solicitudes.service;

import com.example.solicitudes.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Component
public class PersonalClient {
    private final RestClient http;

    public PersonalClient(@Value("${personal.url}") String url) {
        this.http = RestClient.builder().baseUrl(url.trim()).build();
    }

    // Cuantos trabajadores Disponibles hay de una especialidad
    public int disponibles(String especialidad) {
        try {
            Map<?, ?> r = http.get()
                    .uri(u -> u.path("/trabajadores/disponibles").queryParam("especialidad", especialidad).build())
                    .retrieve()
                    .body(Map.class);
            return r == null ? 0 : ((Number) r.get("disponibles")).intValue();
        } catch (RestClientException e) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo verificar la disponibilidad de personal. Intentalo mas tarde.");
        }
    }
}
