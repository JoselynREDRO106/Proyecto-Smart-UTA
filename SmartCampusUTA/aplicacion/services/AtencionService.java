package aplicacion.services;

import dominio.estructuras.Cola;
import dominio.estructuras.ListaCircular;
import dominio.estructuras.ListaSimple;
import dominio.models.Turno;
import dominio.models.Ventanilla;
import persistencia.dao.TurnoDAO;

import java.sql.SQLException;

public class AtencionService {
    private final TurnoDAO turnoDAO = new TurnoDAO();

    public Turno generarTurno(int estudianteId) throws SQLException {
        if (estudianteId <= 0) {
            throw new IllegalArgumentException("El estudiante es obligatorio");
        }
        return turnoDAO.crearTurno(estudianteId);
    }

    public ResultadoAtencion atenderSiguiente() throws SQLException {
        Cola<Turno> cola = cargarColaPendiente();
        ListaCircular<Ventanilla> ventanillas = turnoDAO.ventanillasActivas();
        if (cola.estaVacia()) {
            throw new IllegalStateException("No existen turnos pendientes");
        }
        if (ventanillas.estaVacia()) {
            throw new IllegalStateException("No existen ventanillas activas");
        }
        Turno turno = cola.desencolar();
        Ventanilla ventanilla = ventanillas.siguiente();
        turnoDAO.marcarAtendido(turno.getId(), ventanilla.getId());
        turno.setEstado("atendido");
        turno.setVentanillaId(ventanilla.getId());
        return new ResultadoAtencion(turno, ventanilla);
    }

    public Cola<Turno> cargarColaPendiente() throws SQLException {
        ListaSimple<Turno> pendientes = turnoDAO.listarPendientes();
        Cola<Turno> cola = new Cola<>();
        for (Turno turno : pendientes) {
            cola.encolar(turno);
        }
        return cola;
    }

    public record ResultadoAtencion(Turno turno, Ventanilla ventanilla) {
    }
}
