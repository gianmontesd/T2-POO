package pe.pcsac.servicio;

import pe.pcsac.dao.CertificadoDAO;
import pe.pcsac.dao.InscripcionDAO;
import pe.pcsac.modelo.Certificado;
import pe.pcsac.modelo.EstadoInscripcion;
import pe.pcsac.modelo.Inscripcion;

import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** RF-08, RF-09 y RF-10: emision, consulta y descarga de certificados. */
public class CertificadoServicio {

    public static final Path CARPETA = Path.of("certificados");

    private final CertificadoDAO certificadoDAO = new CertificadoDAO();
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private final GeneradorPdf generador = new GeneradorPdf();

    /** RF-09: emite el certificado de una inscripcion aprobada y escribe el PDF. */
    public Certificado emitir(int idInscripcion) throws ServicioException {
        try {
            Inscripcion i = inscripcionDAO.buscarPorId(idInscripcion);
            if (i == null) throw new ServicioException("La inscripcion indicada no existe.");
            if (i.getEstado() != EstadoInscripcion.APROBADO) {
                throw new ServicioException("Solo se certifica a alumnos aprobados. "
                        + i.getAlumno().getNombreCompleto() + " figura como " + i.getEstado() + ".");
            }
            if (certificadoDAO.existePorInscripcion(idInscripcion)) {
                throw new ServicioException("Esta inscripcion ya tiene un certificado emitido.");
            }
            Certificado c = new Certificado(generarCodigo(i), i, LocalDate.now());
            certificadoDAO.insertar(c);
            Path pdf = generador.generar(c, CARPETA);
            c.setRutaArchivo(pdf.toAbsolutePath().toString());
            certificadoDAO.actualizarRuta(c.getIdCertificado(), c.getRutaArchivo());
            return c;
        } catch (SQLException e) {
            throw new ServicioException("Error al emitir el certificado: " + e.getMessage(), e);
        }
    }

    /** Emision por lote de todos los aprobados de una capacitacion. */
    public List<Certificado> emitirLote(int idCapacitacion) throws ServicioException {
        List<Certificado> emitidos = new ArrayList<>();
        try {
            for (Inscripcion i : inscripcionDAO.listarPorCapacitacion(idCapacitacion)) {
                if (i.getEstado() == EstadoInscripcion.APROBADO
                        && !certificadoDAO.existePorInscripcion(i.getIdInscripcion())) {
                    emitidos.add(emitir(i.getIdInscripcion()));
                }
            }
        } catch (SQLException e) {
            throw new ServicioException("Error en la emision por lote: " + e.getMessage(), e);
        }
        return emitidos;
    }

    /** RF-08: consulta de todos los certificados emitidos. */
    public List<Certificado> listar() throws ServicioException {
        try {
            return certificadoDAO.listar();
        } catch (SQLException e) {
            throw new ServicioException("Error al listar certificados: " + e.getMessage(), e);
        }
    }

    /** RF-10: certificados de los trabajadores de una empresa minera. */
    public List<Certificado> listarPorEmpresa(int idEmpresa) throws ServicioException {
        try {
            return certificadoDAO.listarPorEmpresaDelAlumno(idEmpresa);
        } catch (SQLException e) {
            throw new ServicioException("Error al listar certificados: " + e.getMessage(), e);
        }
    }

    private String generarCodigo(Inscripcion i) {
        return "CERT-" + i.getCapacitacion().getCodigo() + "-" + i.getAlumno().getDni();
    }
}
