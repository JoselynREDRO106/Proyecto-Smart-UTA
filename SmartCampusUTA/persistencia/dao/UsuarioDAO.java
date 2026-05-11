package persistencia.dao;

import dominio.models.Usuario;
import persistencia.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UsuarioDAO {
    public Usuario registrar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO Usuario (nombre, email, password, rol_id, activo) VALUES (?, ?, ?, ?, TRUE)";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, PasswordUtil.hash(usuario.getPassword()));
            ps.setInt(4, usuario.getRolId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.setId(keys.getInt(1));
                }
            }
            usuario.setPassword(null);
            usuario.setActivo(true);
            return usuario;
        }
    }

    public Optional<Usuario> autenticar(String email, String passwordPlano) throws SQLException {
        String sql = "SELECT id, nombre, email, password, rol_id, activo FROM Usuario WHERE email = ? AND activo = TRUE";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.verificar(passwordPlano, rs.getString("password"))) {
                    Usuario usuario = mapear(rs);
                    usuario.setPassword(null);
                    return Optional.of(usuario);
                }
                return Optional.empty();
            }
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPassword(rs.getString("password"));
        usuario.setRolId(rs.getInt("rol_id"));
        usuario.setActivo(rs.getBoolean("activo"));
        return usuario;
    }
}
