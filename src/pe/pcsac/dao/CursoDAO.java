package pe.pcsac.dao;

import pe.pcsac.modelo.Curso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Operaciones CRUD de la tabla curso (RF-02). */
public class CursoDAO {

    public int insertar(Curso c) throws SQLException {
        String sql = "INSERT INTO curso (codigo, nombre, duracion_horas, nota_minima) VALUES (?, ?, ?, ?)";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCodigo());
            ps.setString(2, c.getNombre());
            ps.setInt(3, c.getDuracionHoras());
            ps.setDouble(4, c.getNotaMinima());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setIdCurso(rs.getInt(1));
            }
            return c.getIdCurso();
        }
    }

    public Curso buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM curso WHERE id_curso = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public List<Curso> listar() throws SQLException {
        String sql = "SELECT * FROM curso ORDER BY codigo";
        List<Curso> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public boolean actualizar(Curso c) throws SQLException {
        String sql = "UPDATE curso SET codigo = ?, nombre = ?, duracion_horas = ?, nota_minima = ? "
                   + "WHERE id_curso = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getCodigo());
            ps.setString(2, c.getNombre());
            ps.setInt(3, c.getDuracionHoras());
            ps.setDouble(4, c.getNotaMinima());
            ps.setInt(5, c.getIdCurso());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM curso WHERE id_curso = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Curso mapear(ResultSet rs) throws SQLException {
        Curso c = new Curso();
        c.setIdCurso(rs.getInt("id_curso"));
        c.setCodigo(rs.getString("codigo"));
        c.setNombre(rs.getString("nombre"));
        c.setDuracionHoras(rs.getInt("duracion_horas"));
        c.setNotaMinima(rs.getDouble("nota_minima"));
        return c;
    }
}
