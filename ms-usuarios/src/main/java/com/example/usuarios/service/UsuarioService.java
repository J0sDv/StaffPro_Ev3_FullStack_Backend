package com.example.usuarios.service;

import com.example.usuarios.dto.Dtos.*;
import com.example.usuarios.exception.ApiException;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.UsuarioRepository;
import com.example.usuarios.security.JwtService;
import com.example.usuarios.util.Roles;
import com.example.usuarios.util.Rut;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder, JwtService jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    // Autenticacion

    public AuthResponse login(LoginRequest r) {
        String rut = Rut.limpiar(r.rut());
        String clave = r.password() == null ? "" : r.password();
        Usuario u = repo.findByCorreoIgnoreCase(texto(r.correo()))
                .filter(x -> x.getRutLimpio().equals(rut))
                .filter(x -> encoder.matches(clave, x.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED,
                        "Credenciales incorrectas. Verifica tu correo, RUT y contraseña."));
        return new AuthResponse(jwt.generar(u), UsuarioDto.de(u));
    }

    // El registro publico siempre crea cuentas de tipo Cliente
    public AuthResponse registro(RegistroRequest r) {
        if (texto(r.empresa()).length() < 3) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ingresa el nombre de tu empresa (minimo 3 caracteres).");
        }
        UsuarioRequest datos = new UsuarioRequest(r.nombre(), r.rut(), r.correo(), r.telefono(),
                r.password(), Roles.CLIENTE, r.empresa());
        Usuario u = guardar(new Usuario(), datos, true);
        return new AuthResponse(jwt.generar(u), UsuarioDto.de(u));
    }

    // Renovacion: el Gateway ya valido el token y envia el id del usuario
    public AuthResponse refrescar(Long id) {
        Usuario u = buscar(id);
        return new AuthResponse(jwt.generar(u), UsuarioDto.de(u));
    }

    // CRUD (solo Administrador)

    public List<UsuarioDto> listar() {
        return repo.findAll().stream().map(UsuarioDto::de).toList();
    }

    public UsuarioDto obtener(Long id) {
        return UsuarioDto.de(buscar(id));
    }

    public UsuarioDto crear(UsuarioRequest d) {
        return UsuarioDto.de(guardar(new Usuario(), d, true));
    }

    public UsuarioDto actualizar(Long id, UsuarioRequest d) {
        return UsuarioDto.de(guardar(buscar(id), d, false));
    }

    public void eliminar(Long id, Long solicitante) {
        Usuario u = buscar(id);
        if (id.equals(solicitante)) {
            throw new ApiException(HttpStatus.CONFLICT, "No puedes eliminar tu propia cuenta.");
        }
        if (Roles.ADMIN.equals(u.getRol()) && repo.countByRol(Roles.ADMIN) <= 1) {
            throw new ApiException(HttpStatus.CONFLICT, "No se puede eliminar al ultimo Administrador.");
        }
        repo.delete(u);
    }

    // Reglas de negocio

    private Usuario guardar(Usuario u, UsuarioRequest d, boolean claveObligatoria) {
        List<String> errores = new ArrayList<>();
        String correo = texto(d.correo()).toLowerCase();
        String clave = d.password() == null ? "" : d.password();

        if (texto(d.nombre()).length() < 3) errores.add("El nombre debe tener al menos 3 caracteres.");
        if (!Rut.valido(d.rut())) errores.add("El RUT no es valido.");
        boolean formatoCorreo = correo.indexOf('@') > 0 && correo.indexOf('.', correo.indexOf('@')) > 0
                && !correo.contains(" ");
        if (!formatoCorreo || !(correo.endsWith("@duoc.cl") || correo.endsWith("@gmail.com"))) {
            errores.add("El correo debe ser @duoc.cl o @gmail.com.");
        }
        if (!texto(d.telefono()).matches("[+0-9 ]{8,15}")) errores.add("El telefono no es valido.");
        if (d.rol() == null || !Roles.TODOS.contains(d.rol())) errores.add("El rol no es valido.");
        if ((claveObligatoria || !clave.isEmpty()) && (clave.length() < 6 || clave.length() > 10)) {
            errores.add("La contraseña debe tener entre 6 y 10 caracteres.");
        }
        if (!errores.isEmpty()) throw new ApiException(HttpStatus.BAD_REQUEST, String.join(" ", errores));

        repo.findByRutLimpio(Rut.limpiar(d.rut())).filter(o -> !o.getId().equals(u.getId()))
                .ifPresent(o -> { throw new ApiException(HttpStatus.CONFLICT, "Ya existe un usuario con ese RUT."); });
        repo.findByCorreoIgnoreCase(correo).filter(o -> !o.getId().equals(u.getId()))
                .ifPresent(o -> { throw new ApiException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo."); });

        u.setNombre(texto(d.nombre()));
        u.setRut(texto(d.rut()));
        u.setCorreo(correo);
        u.setTelefono(texto(d.telefono()));
        u.setRol(d.rol());
        u.setEmpresa(texto(d.empresa()));
        if (!clave.isEmpty()) u.setPassword(encoder.encode(clave));   // en una edicion, vacia = no cambia
        return repo.save(u);
    }

    private Usuario buscar(Long id) {
        return repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
    }

    private static String texto(String s) {
        return s == null ? "" : s.trim();
    }
}