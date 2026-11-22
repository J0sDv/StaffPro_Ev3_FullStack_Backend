package com.example.personal.service;

import com.example.personal.dto.Dtos.*;
import com.example.personal.exception.ApiException;
import com.example.personal.model.Trabajador;
import com.example.personal.repository.TrabajadorRepository;
import com.example.personal.util.Rut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TrabajadorService {
    public static final String DISPONIBLE = "Disponible";
    public static final String EN_TURNO = "En turno";

    private final TrabajadorRepository repo;

    public TrabajadorService(TrabajadorRepository repo) {
        this.repo = repo;
    }

    public List<Trabajador> listar() {
        return repo.findAll();
    }

    public Trabajador obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Trabajador no encontrado."));
    }

    public Trabajador crear(TrabajadorRequest d) {
        return guardar(new Trabajador(), d);
    }

    public Trabajador actualizar(Long id, TrabajadorRequest d) {
        return guardar(obtener(id), d);
    }

    public void eliminar(Long id) {
        repo.delete(obtener(id));
    }

    // Regla de negocio: solo se asigna a un trabajador Disponible, y pasa a "En turno"
    public Trabajador asignar(Long id, String cliente) {
        Trabajador t = obtener(id);
        if (texto(cliente).length() < 3) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ingresa el nombre del cliente (minimo 3 caracteres).");
        }
        if (!DISPONIBLE.equals(t.getEstado())) {
            throw new ApiException(HttpStatus.CONFLICT, "El trabajador ya esta en turno.");
        }
        t.setCliente(texto(cliente));
        t.setEstado(EN_TURNO);
        return repo.save(t);
    }

    // Lo consulta ms-solicitudes para saber si hay personal suficiente
    public Map<String, Object> disponibles(String especialidad) {
        int cantidad = repo.findByEstadoAndEspecialidad(DISPONIBLE, especialidad).size();
        return Map.of("especialidad", especialidad, "disponibles", cantidad);
    }

    // Cuantos trabajadores tiene asignados un cliente (Panel del Cliente)
    public Map<String, Object> asignados(String cliente) {
        return Map.of("cliente", cliente.trim(), "cantidad", repo.countByClienteIgnoreCase(cliente.trim()));
    }

    // Ficha del propio trabajador, buscada por el RUT del token (Panel del Trabajador)
    public Trabajador miFicha(String rutSesion) {
        return repo.findByRutLimpio(Rut.limpiar(rutSesion))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No tienes ficha de trabajador."));
    }

    private Trabajador guardar(Trabajador t, TrabajadorRequest d) {
        List<String> errores = new ArrayList<>();
        if (texto(d.nombre()).length() < 3) errores.add("El nombre debe tener al menos 3 caracteres.");
        if (!Rut.valido(d.rut())) errores.add("El RUT no es valido.");
        if (texto(d.especialidad()).isEmpty()) errores.add("Selecciona una especialidad.");
        if (d.valorHora() == null || d.valorHora() < 1000) errores.add("El valor hora debe ser de al menos $1.000.");
        if (!DISPONIBLE.equals(d.estado()) && !EN_TURNO.equals(d.estado())) {
            errores.add("El estado debe ser Disponible o En turno.");
        }
        if (!errores.isEmpty()) throw new ApiException(HttpStatus.BAD_REQUEST, String.join(" ", errores));

        repo.findByRutLimpio(Rut.limpiar(d.rut())).filter(o -> !o.getId().equals(t.getId()))
                .ifPresent(o -> { throw new ApiException(HttpStatus.CONFLICT, "Ya existe un trabajador con este RUT."); });

        t.setNombre(texto(d.nombre()));
        t.setRut(texto(d.rut()));
        t.setEspecialidad(texto(d.especialidad()));
        t.setValorHora(d.valorHora());
        t.setCliente(texto(d.cliente()).isEmpty() ? "Sin asignar" : texto(d.cliente()));
        t.setEstado(d.estado());
        return repo.save(t);
    }

    private static String texto(String s) {
        return s == null ? "" : s.trim();
    }
}