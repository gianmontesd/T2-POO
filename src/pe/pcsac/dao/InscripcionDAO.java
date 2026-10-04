package pe.pcsac.dao;

import pe.pcsac.modelo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Operaciones CRUD de la tabla inscripcion (RF-04 a RF-07). */
public class InscripcionDAO {

    private static final String SELECT_BASE =
            "SELECT i.*, "
          + "       a.dni, a.nombres, a.apellidos, a.correo, a.telefono, a.area, a.id_empresa AS al_empresa, "
          + "       ae.ruc AS al_ruc, ae.razon_social AS al_razon, ae.contacto AS al_contacto, "
          + "       ae.telefono AS al_emp_telefono, "
          + "       cap.codigo, cap.id_curso, cap.id_empresa, cap.id_instructor, cap.fecha, cap.nota_minima, "
          + "       cu.codigo AS cu_codigo, cu.nombre AS cu_nombre, cu.duracion_horas, cu.nota_minima AS cu_nota, "
          + "       em.ruc, em.razon_social, em.contacto, em.telefono AS em_telefono, "
          + "       ins.dni AS ins_dni, ins.nombres AS ins_nombres, ins.apellidos AS ins_apellidos, "
          + "       ins.especialidad "
          + "FROM inscripcion i "
          + "INNER JOIN alumno       a   ON a.id_alumno = i.id_alumno "
          + "INNER JOIN empresa      ae  ON ae.id_empresa = a.id_empresa "
          + "INNER JOIN capacitacion cap ON cap.id_capacitacion = i.id_capacitacion "
          + "INNER JOIN curso        cu  ON cu.id_curso = cap.id_curso "
          + "INNER JOIN empresa      em  ON em.id_empresa = cap.id_empresa "
          + "INNER JOIN instructor   ins ON ins.id_instructor = cap.id_instructor";

    public int insertar(Inscripcion i) throws SQLException {
        String sql = "INSERT INTO inscripcion (id_alumno, id_capacitacion, fecha_inscripcion, voucher, "
                   + "nota_final, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, i.getAlumno().getIdAlumno());
            ps.setInt(2, i.getCapacitacion().getIdCapacitacion());
            ps.setDate(3, Date.valueOf(i.getFechaInscripcion()));
            ps.setString(4, i.getVoucher());
            if (i.getNotaFinal() == null) ps.setNull(5, Types.DECIMAL);
            else ps.setDouble(5, i.getNotaFinal());
            ps.setString(6, i.getEstado().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) i.setIdInscripcion(rs.getInt(1));
            }
            return i.getIdInscripcion();
        }
    }

    /** RF-04: comprueba el duplicado antes de insertar. */
    public boolean existe(int idAlumno, int idCapacitacion) throws SQLException {
        String sql = "SELECT 1 FROM inscripcion WHERE id_alumno = ? AND id_capacitacion = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idAlumno);
            ps.setInt(2, idCapacitacion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Inscripcion buscarPorId(int id) throws SQLException {
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(SELECT_BASE + " WHERE i.id_inscripcion = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public Inscripcion buscarPorDniYCapacitacion(String dni, int idCapacitacion) throws SQLException {
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(
                     SELECT_BASE + " WHERE a.dni = ? AND i.id_capacitacion = ?")) {
            ps.setString(1, dni);
            ps.setInt(2, idCapacitacion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public List<Inscripcion> listar() throws SQLException {
        return consultar(SELECT_BASE + " ORDER BY cap.fecha DESC, a.apellidos", null);
    }

    public List<Inscripcion> listarPorCapacitacion(int idCapacitacion) throws SQLException {
        return consultar(SELECT_BASE + " WHERE i.id_capacitacion = ? ORDER BY a.apellidos, a.nombres",
                         idCapacitacion);
    }

    /** RF-10: inscripciones de los trabajadores de una empresa minera. */
    public List<Inscripcion> listarPorEmpresaDelAlumno(int idEmpresa) throws SQLException {
        return consultar(SELECT_BASE + " WHERE a.id_empresa = ? ORDER BY cap.fecha DESC, a.apellidos",
                         idEmpresa);
    }

    private List<Inscripcion> consultar(String sql, Integer parametro) throws SQLException {
        List<Inscripcion> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (parametro != null) ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** RF-06 y RF-07: guarda la nota y el estado ya calculado por el modelo. */
    public boolean actualizarNota(Inscripcion i) throws SQLException {
        String sql = "UPDATE inscripcion SET nota_final = ?, estado = ? WHERE id_inscripcion = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (i.getNotaFinal() == null) ps.setNull(1, Types.DECIMAL);
            else ps.setDouble(1, i.getNotaFinal());
            ps.setString(2, i.getEstado().name());
            ps.setInt(3, i.getIdInscripcion());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Inscripcion i) throws SQLException {
        String sql = "UPDATE inscripcion SET id_alumno = ?, id_capacitacion = ?, fecha_inscripcion = ?, "
                   + "voucher = ?, nota_final = ?, estado = ? WHERE id_inscripcion = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, i.getAlumno().getIdAlumno());
            ps.setInt(2, i.getCapacitacion().getIdCapacitacion());
            ps.setDate(3, Date.valueOf(i.getFechaInscripcion()));
            ps.setString(4, i.getVoucher());
            if (i.getNotaFinal() == null) ps.setNull(5, Types.DECIMAL);
            else ps.setDouble(5, i.getNotaFinal());
            ps.setString(6, i.getEstado().name());
            ps.setInt(7, i.getIdInscripcion());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM inscripcion WHERE id_inscripcion = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    static Inscripcion mapear(ResultSet rs) throws SQLException {
        Empresa empAlumno = new Empresa();
        empAlumno.setIdEmpresa(rs.getInt("al_empresa"));
        empAlumno.setRuc(rs.getString("al_ruc"));
        empAlumno.setRazonSocial(rs.getString("al_razon"));
        empAlumno.setContacto(rs.getString("al_contacto"));
        empAlumno.setTelefono(rs.getString("al_emp_telefono"));

        Alumno a = new Alumno();
        a.setIdAlumno(rs.getInt("id_alumno"));
        a.setDni(rs.getString("dni"));
        a.setNombres(rs.getString("nombres"));
        a.setApellidos(rs.getString("apellidos"));
        a.setCorreo(rs.getString("correo"));
        a.setTelefono(rs.getString("telefono"));
        a.setArea(rs.getString("area"));
        a.setEmpresa(empAlumno);

        Capacitacion cap = CapacitacionDAO.mapear(rs);

        Inscripcion i = new Inscripcion();
        i.setIdInscripcion(rs.getInt("id_inscripcion"));
        i.setAlumno(a);
        i.setCapacitacion(cap);
        i.setFechaInscripcion(rs.getDate("fecha_inscripcion").toLocalDate());
        i.setVoucher(rs.getString("voucher"));
        double nota = rs.getDouble("nota_final");
        Double notaFinal = rs.wasNull() ? null : nota;
        i.restaurarDesdeBD(notaFinal, EstadoInscripcion.valueOf(rs.getString("estado")));
        return i;
    }
}
