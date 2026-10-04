package pe.pcsac.dao;

import pe.pcsac.modelo.Instructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Operaciones CRUD de la tabla instructor. */
public class InstructorDAO {

    public int insertar(Instructor i) throws SQLException {
        String sql = "INSERT INTO instructor (dni, nombres, apellidos, especialidad) VALUES (?, ?, ?, ?)";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, i.getDni());
            ps.setString(2, i.getNombres());
            ps.setString(3, i.getApellidos());
            ps.setString(4, i.getEspecialidad());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) i.setIdInstructor(rs.getInt(1));
            }
            return i.getIdInstructor();
        }
    }

    public Instructor buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM instructor WHERE id_instructor = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public List<Instructor> listar() throws SQLException {
        String sql = "SELECT * FROM instructor ORDER BY apellidos, nombres";
        List<Instructor> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public boolean actualizar(Instructor i) throws SQLException {
        String sql = "UPDATE instructor SET dni = ?, nombres = ?, apellidos = ?, especialidad = ? "
                   + "WHERE id_instructor = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, i.getDni());
            ps.setString(2, i.getNombres());
            ps.setString(3, i.getApellidos());
            ps.setString(4, i.getEspecialidad());
            ps.setInt(5, i.getIdInstructor());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM instructor WHERE id_instructor = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Instructor mapear(ResultSet rs) throws SQLException {
        Instructor i = new Instructor();
        i.setIdInstructor(rs.getInt("id_instructor"));
        i.setDni(rs.getString("dni"));
        i.setNombres(rs.getString("nombres"));
        i.setApellidos(rs.getString("apellidos"));
        i.setEspecialidad(rs.getString("especialidad"));
        return i;
    }
}
