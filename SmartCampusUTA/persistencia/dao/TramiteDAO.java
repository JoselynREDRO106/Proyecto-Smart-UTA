package persistencia.dao;

import dominio.models.HistorialTramite;
import dominio.models.Tramite;
import persistencia.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TramiteDAO {
    public Tramite crear(Tramite tramite) throws SQLException {
        String sql = """
                INSERT INTO Tramite (codigo_unico, tipo_tramite_id, estudiante_id, descripcion, estado, prioridad, observaciones)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tramite.getCodigoUnico());
            ps.setInt(2, tramite.getTipoTramiteId());
            ps.setInt(3, tramite.getEstudianteId());
            ps.setString(4, tramite.getDescripcion());
            ps.setString(5, tramite.getEstado());
            ps.setString(6, tramite.getPrioridad());
            ps.setString(7, tramite.getObservaciones());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    tramite.setId(keys.getInt(1));
                }
            }
            return tramite;
        }
    }

    public void cambiarEstado(int tramiteId, int usuarioId, String estadoNuevo, String comentario) throws SQLException {
        String estadoAnterior = obtenerEstado(tramiteId);
        try (Connection cn = DatabaseConnection.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement update = cn.prepareStatement("UPDATE Tramite SET estado = ?, observaciones = ? WHERE id = ?");
                 PreparedStatement historial = cn.prepareStatement("""
                         INSERT INTO HistorialTramite (tramite_id, usuario_id, estado_anterior, estado_nuevo, comentario)
                         VALUES (?, ?, ?, ?, ?)
                         """)) {
                update.setString(1, estadoNuevo);
                update.setString(2, comentario);
                update.setInt(3, tramiteId);
                update.executeUpdate();

                historial.setInt(1, tramiteId);
                historial.setInt(2, usuarioId);
                historial.setString(3, estadoAnterior);
                historial.setString(4, estadoNuevo);
                historial.setString(5, comentario);
                historial.executeUpdate();
                cn.commit();
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            }
        }
    }

    public String obtenerEstado(int tramiteId) throws SQLException {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT estado FROM Tramite WHERE id = ?")) {
            ps.setInt(1, tramiteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Tramite no encontrado: " + tramiteId);
                }
                return rs.getString("estado");
            }
        }
    }

    public dominio.estructuras.ListaSimple<HistorialTramite> historial(int tramiteId) throws SQLException {
        dominio.estructuras.ListaSimple<HistorialTramite> lista = new dominio.estructuras.ListaSimple<>();
        String sql = "SELECT * FROM HistorialTramite WHERE tramite_id = ? ORDER BY fecha_cambio ASC";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, tramiteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HistorialTramite h = new HistorialTramite();
                    h.setId(rs.getInt("id"));
                    h.setTramiteId(rs.getInt("tramite_id"));
                    h.setUsuarioId(rs.getInt("usuario_id"));
                    h.setEstadoAnterior(rs.getString("estado_anterior"));
                    h.setEstadoNuevo(rs.getString("estado_nuevo"));
                    h.setComentario(rs.getString("comentario"));
                    h.setFechaCambio(rs.getTimestamp("fecha_cambio").toLocalDateTime());
                    lista.agregarFinal(h);
                }
            }
        }
        return lista;
    }
}
