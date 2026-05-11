package persistencia.dao;

import dominio.estructuras.ListaCircular;
import dominio.estructuras.ListaSimple;
import dominio.models.Turno;
import dominio.models.Ventanilla;
import persistencia.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TurnoDAO {
    public Turno crearTurno(int estudianteId) throws SQLException {
        String sql = "INSERT INTO Turno (numero, estudiante_id, estado) VALUES ((SELECT COALESCE(MAX(numero), 0) + 1 FROM Turno), ?, 'pendiente')";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, estudianteId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    Turno turno = new Turno();
                    turno.setId(keys.getInt(1));
                    turno.setEstudianteId(estudianteId);
                    turno.setEstado("pendiente");
                    return turno;
                }
            }
            throw new SQLException("No se pudo crear el turno");
        }
    }

    public ListaSimple<Turno> listarPendientes() throws SQLException {
        ListaSimple<Turno> turnos = new ListaSimple<>();
        String sql = "SELECT id, numero, estudiante_id, estado, ventanilla_id, fecha_creacion FROM Turno WHERE estado = 'pendiente' ORDER BY numero ASC";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Turno t = new Turno();
                t.setId(rs.getInt("id"));
                t.setNumero(rs.getInt("numero"));
                t.setEstudianteId(rs.getInt("estudiante_id"));
                t.setEstado(rs.getString("estado"));
                int ventanillaId = rs.getInt("ventanilla_id");
                t.setVentanillaId(rs.wasNull() ? null : ventanillaId);
                t.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
                turnos.agregarFinal(t);
            }
        }
        return turnos;
    }

    public ListaCircular<Ventanilla> ventanillasActivas() throws SQLException {
        ListaCircular<Ventanilla> ventanillas = new ListaCircular<>();
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT id, nombre, ubicacion, activa FROM Ventanilla WHERE activa = TRUE ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Ventanilla v = new Ventanilla();
                v.setId(rs.getInt("id"));
                v.setNombre(rs.getString("nombre"));
                v.setUbicacion(rs.getString("ubicacion"));
                v.setActiva(rs.getBoolean("activa"));
                ventanillas.agregar(v);
            }
        }
        return ventanillas;
    }

    public void marcarAtendido(int turnoId, int ventanillaId) throws SQLException {
        String sql = "UPDATE Turno SET estado = 'atendido', ventanilla_id = ?, fecha_atencion = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, ventanillaId);
            ps.setInt(2, turnoId);
            ps.executeUpdate();
        }
    }
}
