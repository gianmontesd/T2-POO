package pe.pcsac.dao;

import pe.pcsac.modelo.Certificado;
import pe.pcsac.modelo.Inscripcion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Operaciones CRUD de la tabla certificado (RF-08, RF-09, RF-10). */
public class CertificadoDAO {

    private static final String SELECT_BASE =
            "SELECT c.id_certificado, c.codigo AS cert_codigo, c.fecha_emision, c.ruta_archivo, "
          + "       i.*, "
          + "       a.dni, a.nombres, a.apellidos, a.correo, a.telefono, a.area, a.id_empresa AS al_empresa, "
          + "       ae.ruc AS al_ruc, ae.razon_social AS al_razon, ae.contacto AS al_contacto, "
          + "       ae.telefono AS al_emp_telefono, "
          + "       cap.codigo, cap.id_curso, cap.id_empresa, cap.id_instructor, cap.fecha, cap.nota_minima, "
          + "       cu.codigo AS cu_codigo, cu.nombre AS cu_nombre, cu.duracion_horas, cu.nota_minima AS cu_nota, "
          + "       em.ruc, em.razon_social, em.contacto, em.telefono AS em_telefono, "
          + "       ins.dni AS ins_dni, ins.nombres AS ins_nombres, ins.apellidos AS ins_apellidos, "
          + "       ins.especialidad "
          + "FROM certificado c "
          + "INNER JOIN inscripcion  i   ON i.id_inscripcion = c.id_inscripcion "
          + "INNER JOIN alumno       a   ON a.id_alumno = i.id_alumno "
          + "INNER JOIN empresa      ae  ON ae.id_empresa = a.id_empresa "
          + "INNER JOIN capacitacion cap ON cap.id_capacitacion = i.id_capacitacion "
          + "INNER JOIN curso        cu  ON cu.id_curso = cap.id_curso "
          + "INNER JOIN empresa      em  ON em.id_empresa = cap.id_empresa "
          + "INNER JOIN instructor   ins ON ins.id_instructor = cap.id_instructor";

    public int insertar(Certificado c) throws SQLException {
        String sql = "INSERT INTO certificado (codigo, id_inscripcion, fecha_emision, ruta_archivo) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCodigo());
            ps.setInt(2, c.getInscripcion().getIdInscripcion());
            ps.setDate(3, Date.valueOf(c.getFechaEmision()));
            ps.setString(4, c.getRutaArchivo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setIdCertificado(rs.getInt(1));
            }
            return c.getIdCertificado();
        }
    }

    public boolean existePorInscripcion(int idInscripcion) throws SQLException {
        String sql = "SELECT 1 FROM certificado WHERE id_inscripcion = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Certificado buscarPorCodigo(String codigo) throws SQLException {
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(SELECT_BASE + " WHERE c.codigo = ?")) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** RF-08: todos los certificados emitidos. */
    public List<Certificado> listar() throws SQLException {
        return consultar(SELECT_BASE + " ORDER BY c.fecha_emision DESC, c.codigo", null);
    }

    /** RF-10: certificados de los trabajadores de una empresa minera. */
    public List<Certificado> listarPorEmpresaDelAlumno(int idEmpresa) throws SQLException {
        return consultar(SELECT_BASE + " WHERE a.id_empresa = ? ORDER BY c.fecha_emision DESC", idEmpresa);
    }

    private List<Certificado> consultar(String sql, Integer parametro) throws SQLException {
        List<Certificado> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (parametro != null) ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public boolean actualizarRuta(int idCertificado, String ruta) throws SQLException {
        String sql = "UPDATE certificado SET ruta_archivo = ? WHERE id_certificado = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, ruta);
            ps.setInt(2, idCertificado);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM certificado WHERE id_certificado = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Certificado mapear(ResultSet rs) throws SQLException {
        Inscripcion i = InscripcionDAO.mapear(rs);
        Certificado c = new Certificado();
        c.setIdCertificado(rs.getInt("id_certificado"));
        c.setCodigo(rs.getString("cert_codigo"));
        c.setInscripcion(i);
        c.setFechaEmision(rs.getDate("fecha_emision").toLocalDate());
        c.setRutaArchivo(rs.getString("ruta_archivo"));
        return c;
    }
}
