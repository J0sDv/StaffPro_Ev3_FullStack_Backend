package com.example.solicitudes.config;

import com.example.solicitudes.model.Solicitud;
import com.example.solicitudes.repository.SolicitudRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CargaInicial implements CommandLineRunner {
    private final SolicitudRepository repo;

    public CargaInicial(SolicitudRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        crear("CT002", "Operador de grua", 1, "2026-10-01", "2026-12-15", "Faena en Santiago Centro.", "En servicio", "2026-09-20");
        crear("CT001", "Electricista", 1, "2026-10-05", "2026-11-30", "", "En servicio", "2026-09-25");
        crear("CT001", "Conductor", 2, "2026-11-02", "2026-12-20", "Turno de mañana.", "Solicitado", "2026-10-15");
    }

    private void crear(String contrato, String especialidad, int cantidad, String inicio, String termino,
                       String comentarios, String estado, String creada) {
        Solicitud s = new Solicitud();
        s.setUsuarioId(5L);
        s.setEmpresa("Construccion SA");
        s.setContacto("Camila Reyes");
        s.setCorreo("camila.reyes@gmail.com");
        s.setTelefono("+56 9 5555 5555");
        s.setTipoContrato(contrato);
        s.setEspecialidad(especialidad);
        s.setCantidad(cantidad);
        s.setFechaInicio(inicio);
        s.setFechaTermino(termino);
        s.setComentarios(comentarios);
        s.setEstado(estado);
        s.setFechaCreacion(creada);
        repo.save(s);
    }
}