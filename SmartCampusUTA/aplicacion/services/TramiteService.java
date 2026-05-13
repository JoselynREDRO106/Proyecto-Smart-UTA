package aplicacion.services;

import dominio.estructuras.ListaDoble;
import dominio.estructuras.ListaSimple;
import dominio.estructuras.Pila;
import dominio.models.HistorialTramite;
import dominio.models.Tramite;
import persistencia.dao.TramiteDAO;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class TramiteService {
    private final TramiteDAO tramiteDAO = new TramiteDAO();
    private final Pila<CambioEstado> cambiosRecientes = new Pila<>();

    public Tramite registrarTramite(int tipoTramiteId, int estudianteId, String descripcion, String prioridad) throws SQLException {
        if (tipoTramiteId <= 0 || estudianteId <= 0 || descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("Tipo, estudiante y descripcion son obligatorios");
        }
        Tramite tramite = new Tramite();
        tramite.setCodigoUnico("TRAM-" + LocalDateTime.now().getYear() + "-" + System.nanoTime());
        tramite.setTipoTramiteId(tipoTramiteId);
        tramite.setEstudianteId(estudianteId);
        tramite.setDescripcion(descripcion.trim());
        tramite.setEstado("pendiente");
        tramite.setPrioridad(prioridad == null || prioridad.isBlank() ? "media" : prioridad);
        return tramiteDAO.crear(tramite);
    }

    public void cambiarEstado(int tramiteId, int usuarioId, String estadoNuevo, String comentario) throws SQLException {
        String estadoAnterior = tramiteDAO.obtenerEstado(tramiteId);
        tramiteDAO.cambiarEstado(tramiteId, usuarioId, estadoNuevo, comentario);
        cambiosRecientes.apilar(new CambioEstado(tramiteId, usuarioId, estadoAnterior, estadoNuevo));
    }

    public void deshacerUltimoCambio() throws SQLException {
        if (cambiosRecientes.estaVacia()) {
            throw new IllegalStateException("No hay cambios para deshacer");
        }
        CambioEstado cambio = cambiosRecientes.desapilar();
        tramiteDAO.cambiarEstado(cambio.tramiteId(), cambio.usuarioId(), cambio.estadoAnterior(), "Deshacer cambio desde pila LIFO");
    }

    public ListaSimple<HistorialTramite> historialSimple(int tramiteId) throws SQLException {
        return tramiteDAO.historial(tramiteId);
    }

    public java.util.List<Tramite> listarTramites() throws SQLException {
        return tramiteDAO.listarTodos();
    }

    public String obtenerEstado(int tramiteId) throws SQLException {
        return tramiteDAO.obtenerEstado(tramiteId);
    }

    public ListaDoble<HistorialTramite> expedienteNavegable(int tramiteId) throws SQLException {
        ListaSimple<HistorialTramite> historial = tramiteDAO.historial(tramiteId);
        ListaDoble<HistorialTramite> expediente = new ListaDoble<>();
        for (HistorialTramite item : historial) {
            expediente.agregarFinal(item);
        }
        return expediente;
    }

    private record CambioEstado(int tramiteId, int usuarioId, String estadoAnterior, String estadoNuevo) {
    }
}
