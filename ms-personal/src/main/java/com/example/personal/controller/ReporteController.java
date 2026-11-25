package com.example.personal.controller;

import com.example.personal.dto.Dtos.ReporteHoras;
import com.example.personal.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes")
@Tag(name = "Reportes", description = "Horas trabajadas y costos")
public class ReporteController {
    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    @Operation(summary = "Horas y costo por trabajador y totales por cliente (desde y hasta opcionales)")
    @GetMapping("/horas")
    public ReporteHoras horas(@RequestParam(required = false) String desde,
                              @RequestParam(required = false) String hasta) {
        return service.horas(desde, hasta);
    }
}