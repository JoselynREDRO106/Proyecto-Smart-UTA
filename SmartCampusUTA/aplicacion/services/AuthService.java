package aplicacion.services;

import dominio.models.Usuario;
import persistencia.dao.UsuarioDAO;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario registrar(String nombre, String email, String password, int rolId) throws SQLException {
        if (nombre == null || nombre.isBlank() || email == null || !email.contains("@") || password == null || password.length() < 6) {
            throw new IllegalArgumentException("Datos de registro invalidos");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setEmail(email.trim().toLowerCase());
        usuario.setPassword(password);
        usuario.setRolId(rolId);
        return usuarioDAO.registrar(usuario);
    }

    public Optional<Usuario> autenticar(String email, String password) throws SQLException {
        if (email == null || password == null) {
            return Optional.empty();
        }
        return usuarioDAO.autenticar(email.trim().toLowerCase(), password);
    }
}
