package pe.pcsac.servicio;

import pe.pcsac.dao.*;
import pe.pcsac.modelo.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Reglas de inscripcion y de calificacion. La vista nunca habla directamente
 * con los DAO para estas operaciones: pasa por aqui.
 */
public class InscripcionServicio {

    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private final CapacitacionDAO capacitacionDAO = new CapacitacionDAO();

    /** RF-04 y RF-05: inscribe validando que no exista duplicado. */
    public Inscripcion inscribir(int idAlumno, int idCapacitacion, String voucher)
            throws ServicioException {
        try {
            Alumno a = alumnoDAO.buscarPorId(idAlumno);
            if (a == null) throw new ServicioException("El alumno indicado no existe.");
            Capacitacion c = capacitacionDAO.buscarPorId(idCapacitacion);
            if (c == null) throw new ServicioException("La capacitacion indicada no existe.");
            if (inscripcionDAO.existe(idAlumno, idCapacitacion)) {
                throw new ServicioException(
                        "El alumno " + a.getNombreCompleto() + " ya esta inscrito en " + c.getCodigo() + ".");
            }
            Inscripcion i = new Inscripcion(a, c, LocalDate.now(), voucher);
            inscripcionDAO.insertar(i);
            return i;
        } catch (SQLException e) {
            throw new ServicioException("Error al registrar la inscripcion: " + e.getMessage(), e);
        }
    }

    /** RF-06 y RF-07: registra la nota; el estado lo deduce el propio modelo. */
    public Inscripcion registrarNota(int idInscripcion, double nota) throws ServicioException {
        try {
            Inscripcion i = inscripcionDAO.buscarPorId(idInscripcion);
            if (i == null) throw new ServicioException("La inscripcion indicada no existe.");
            i.registrarNota(nota);
            inscripcionDAO.actualizarNota(i);
            return i;
        } catch (IllegalArgumentException e) {
            throw new ServicioException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new ServicioException("Error al registrar la nota: " + e.getMessage(), e);
        }
    }

    public List<Inscripcion> listar() throws ServicioException {
        try {
            return inscripcionDAO.listar();
        } catch (SQLException e) {
            throw new ServicioException("Error al listar inscripciones: " + e.getMessage(), e);
        }
    }

    public List<Inscripcion> listarPorCapacitacion(int idCapacitacion) throws ServicioException {
        try {
            return inscripcionDAO.listarPorCapacitacion(idCapacitacion);
        } catch (SQLException e) {
            throw new ServicioException("Error al listar inscripciones: " + e.getMessage(), e);
        }
    }

    public Inscripcion buscarPorDniYCapacitacion(String dni, int idCapacitacion)
            throws ServicioException {
        try {
            return inscripcionDAO.buscarPorDniYCapacitacion(dni, idCapacitacion);
        } catch (SQLException e) {
            throw new ServicioException("Error al buscar la inscripcion: " + e.getMessage(), e);
        }
    }
}
