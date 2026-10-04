package pe.pcsac.dao;

import pe.pcsac.modelo.Alumno;
import pe.pcsac.modelo.Empresa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Operaciones CRUD de la tabla alumno (RF-01). */
public class AlumnoDAO {

    public int insertar(Alumno a) throws SQLException {
        String sql = "INSERT INTO alumno (dni, nombres, apellidos, correo, telefono, area, id_empresa) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getDni());
            ps.setString(2, a.getNombres());
            ps.setString(3, a.getApellidos());
            ps.setString(4, a.getCorreo());
            ps.setString(5, a.getTelefono());
            ps.setString(6, a.getArea());
            ps.setInt(7, a.getEmpresa().getIdEmpresa());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setIdAlumno(rs.getInt(1));
            }
            return a.getIdAlumno();
        }
    }

    public Alumno buscarPorId(int id) throws SQLException {
        return buscarPor("a.id_alumno = ?", String.valueOf(id), true);
    }

    public Alumno buscarPorDni(String dni) throws SQLException {
        return buscarPor("a.dni = ?", dni, false);
    }

    private Alumno buscarPor(String condicion, String valor, boolean esEntero) throws SQLException {
        String sql = SELECT_BASE + " WHERE " + condicion;
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (esEntero) ps.setInt(1, Integer.parseInt(valor)); else ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public List<Alumno> listar() throws SQLException {
        List<Alumno> lista = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY a.apellidos, a.nombres";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** RF-10: los alumnos de una empresa minera concreta. */
    public List<Alumno> listarPorEmpresa(int idEmpresa) throws SQLException {
        List<Alumno> lista = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE a.id_empresa = ? ORDER BY a.apellidos, a.nombres";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public boolean actualizar(Alumno a) throws SQLException {
        String sql = "UPDATE alumno SET dni = ?, nombres = ?, apellidos = ?, correo = ?, "
                   + "telefono = ?, area = ?, id_empresa = ? WHERE id_alumno = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, a.getDni());
            ps.setString(2, a.getNombres());
            ps.setString(3, a.getApellidos());
            ps.setString(4, a.getCorreo());
            ps.setString(5, a.getTelefono());
            ps.setString(6, a.getArea());
            ps.setInt(7, a.getEmpresa().getIdEmpresa());
            ps.setInt(8, a.getIdAlumno());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM alumno WHERE id_alumno = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private static final String SELECT_BASE =
            "SELECT a.*, e.ruc, e.razon_social, e.contacto, e.telefono AS emp_telefono "
          + "FROM alumno a INNER JOIN empresa e ON e.id_empresa = a.id_empresa";

    private Alumno mapear(ResultSet rs) throws SQLException {
        Empresa e = new Empresa();
        e.setIdEmpresa(rs.getInt("id_empresa"));
        e.setRuc(rs.getString("ruc"));
        e.setRazonSocial(rs.getString("razon_social"));
        e.setContacto(rs.getString("contacto"));
        e.setTelefono(rs.getString("emp_telefono"));

        Alumno a = new Alumno();
        a.setIdAlumno(rs.getInt("id_alumno"));
        a.setDni(rs.getString("dni"));
        a.setNombres(rs.getString("nombres"));
        a.setApellidos(rs.getString("apellidos"));
        a.setCorreo(rs.getString("correo"));
        a.setTelefono(rs.getString("telefono"));
        a.setArea(rs.getString("area"));
        a.setEmpresa(e);
        return a;
    }
}
