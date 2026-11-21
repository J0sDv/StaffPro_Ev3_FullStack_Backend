package com.example.usuarios.config;

import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.UsuarioRepository;
import com.example.usuarios.util.Roles;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CargaInicial implements CommandLineRunner {
    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public CargaInicial(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        crear("Valentina Morales", "12.345.678-5", "admin.staffpro@gmail.com", "+56 9 1111 1111", "admin123", Roles.ADMIN, "");
        crear("Sofia Herrera", "23.456.789-6", "rrhh.staffpro@gmail.com", "+56 9 2222 2222", "rrhh1234", Roles.RRHH, "");
        crear("Matias Fuentes", "18.765.432-7", "coord.staffpro@gmail.com", "+56 9 3333 3333", "coord123", Roles.COORDINADOR, "");
        crear("Javiera Nuñez", "15.234.567-4", "recluta.staffpro@gmail.com", "+56 9 4444 4444", "recluta12", Roles.RECLUTADOR, "");
        crear("Camila Reyes", "17.654.321-3", "camila.reyes@gmail.com", "+56 9 5555 5555", "cliente12", Roles.CLIENTE, "Construccion SA");
        crear("Juan Perez", "11.111.111-1", "juan.perez@gmail.com", "+56 9 6666 6666", "trabajo12", Roles.TRABAJADOR, "");
    }

    private void crear(String nombre, String rut, String correo, String tel, String clave, String rol, String empresa) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setRut(rut);
        u.setCorreo(correo);
        u.setTelefono(tel);
        u.setPassword(encoder.encode(clave));
        u.setRol(rol);
        u.setEmpresa(empresa);
        repo.save(u);
    }
}