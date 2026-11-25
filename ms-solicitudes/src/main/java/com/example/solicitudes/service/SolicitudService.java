package com.example.solicitudes.service;

import com.example.solicitudes.dto.Dtos.*;
import com.example.solicitudes.exception.ApiException;
import com.example.solicitudes.model.Solicitud;
import com.example.solicitudes.repository.SolicitudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SolicitudService {
    private static final ZoneId CHILE = ZoneId.of("America/Santiago");
    private static final List<String> ESTADOS = List.of("Solicitado", "En seleccion", "Asignado", "En servicio", "Finalizado");
    private static final List<String> CONTRATOS = List.of("CT001", "CT002", "CT003", "CT004");
    private static final String CLIENTE = "Cliente";

    private final SolicitudRepository repo;
    private final PersonalClient personal;

    public SolicitudService(SolicitudRepository repo, PersonalClient personal) {
        this.repo = repo;
        this.personal = personal;
    }

    // Un Cliente solo ve sus solicitudes, los demas roles ven todas
    public List<Solicitud> listar(String rol, Long usuarioId) {
        if (CLIENTE.equals(rol) && usuarioId != null) return repo.findByUsuarioId(usuarioId);
        return repo.findAll();
    }

    public Solicitud obtener(Long id, String rol, Long usuarioId) {
        Solicitud s = buscar(id);
        if (CLIENTE.equals(rol) && !s.getUsuarioId().equals(usuarioId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Esa solicitud no es tuya.");
        }
        return s;
    }

    public Solicitud crear(SolicitudRequest d, Long usuarioId) {
        if (usuarioId == null) throw new ApiException(HttpStatus.UNAUTHORIZED, "Falta la identidad del usuario.");
        validar(d);

        // Regla de negocio: no se puede pedir mas personal del que hay disponible
        if (!"Otro".equals(d.especialidad())) {
            int disponibles = personal.disponibles(d.especialidad());
            if (d.cantidad() > disponibles) {
                throw new ApiException(HttpStatus.CONFLICT,
                        "Solo hay " + disponibles + " trabajador(es) disponible(s) para '" + d.especialidad()
                                + "' y solicitaste " + d.cantidad() + ".",
                        Map.of("disponibles", disponibles));
            }
        }

        Solicitud s = new Solicitud();
        s.setUsuarioId(usuarioId);
        s.setEmpresa(texto(d.empresa()));
        s.setContacto(texto(d.contacto()));
        s.setCorreo(texto(d.correo()));
        s.setTelefono(texto(d.telefono()));
        s.setTipoContrato(d.tipoContrato());
        s.setEspecialidad(texto(d.especialidad()));
        s.setCantidad(d.cantidad());
        s.setFechaInicio(d.fechaInicio());
        s.setFechaTermino(d.fechaTermino());
        s.setComentarios(texto(d.comentarios()));
        s.setEstado("Solicitado");
        s.setFechaCreacion(LocalDate.now(CHILE).toString());
        return repo.save(s);
    }

    // Regla: una solicitud finalizada no se puede reabrir
    public Solicitud cambiarEstado(Long id, String estado) {
        if (!ESTADOS.contains(estado)) throw new ApiException(HttpStatus.BAD_REQUEST, "El estado no es valido.");
        Solicitud s = buscar(id);
        if ("Finalizado".equals(s.getEstado()) && !"Finalizado".equals(estado)) {
            throw new ApiException(HttpStatus.CONFLICT, "Una solicitud finalizada no se puede reabrir.");
        }
        s.setEstado(estado);
        return repo.save(s);
    }

    // validaciones  

    private void validar(SolicitudRequest d) {
        List<String> e = new ArrayList<>();
        String correo = texto(d.correo());
        if (texto(d.empresa()).length() < 3) e.add("Ingresa el nombre de tu empresa (minimo 3 caracteres).");
        if (texto(d.contacto()).length() < 3) e.add("Ingresa el nombre de la persona de contacto.");
        if (correo.indexOf('@') <= 0 || correo.indexOf('.', correo.indexOf('@')) < 0 || correo.contains(" ")) {
            e.add("Ingresa un correo electronico valido.");
        }
        if (!texto(d.telefono()).matches("[+0-9 ]{8,15}")) e.add("Ingresa un telefono valido.");
        if (d.tipoContrato() == null || !CONTRATOS.contains(d.tipoContrato())) e.add("Selecciona el tipo de contrato requerido.");
        if (texto(d.especialidad()).isEmpty()) e.add("Selecciona la especialidad que necesitas.");
        if (d.cantidad() == null || d.cantidad() < 1 || d.cantidad() > 50) {
            e.add("La cantidad debe ser un numero entero entre 1 y 50.");
        }
        LocalDate inicio = fecha(d.fechaInicio());
        LocalDate termino = fecha(d.fechaTermino());
        if (inicio == null) e.add("Selecciona la fecha de inicio (AAAA-MM-DD).");
        else if (inicio.isBefore(LocalDate.now(CHILE))) e.add("La fecha de inicio no puede estar en el pasado.");
        if (termino == null) e.add("Selecciona la fecha de termino (AAAA-MM-DD).");
        else if (inicio != null && termino.isBefore(inicio)) e.add("La fecha de termino no puede ser anterior a la de inicio.");
        if (texto(d.comentarios()).length() > 500) e.add("Los comentarios admiten maximo 500 caracteres.");
        if (!e.isEmpty()) throw new ApiException(HttpStatus.BAD_REQUEST, String.join(" ", e));
    }

    private Solicitud buscar(Long id) {
        return repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Solicitud no encontrada."));
    }

    private static LocalDate fecha(String s) {
        try {
            return LocalDate.parse(s);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static String texto(String s) {
        return s == null ? "" : s.trim();
    }
}
