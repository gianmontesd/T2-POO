package pe.pcsac.dao;

import pe.pcsac.modelo.Empresa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Operaciones CRUD de la tabla empresa (RF-03). */
public class EmpresaDAO {

    public int insertar(Empresa e) throws SQLException {
        String sql = "INSERT INTO empresa (ruc, razon_social, contacto, telefono) VALUES (?, ?, ?, ?)";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getRuc());
            ps.setString(2, e.getRazonSocial());
            ps.setString(3, e.getContacto());
            ps.setString(4, e.getTelefono());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setIdEmpresa(rs.getInt(1));
            }
            return e.getIdEmpresa();
        }
    }

    public Empresa buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM empresa WHERE id_empresa = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public Empresa buscarPorRuc(String ruc) throws SQLException {
        String sql = "SELECT * FROM empresa WHERE ruc = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, ruc);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public List<Empresa> listar() throws SQLException {
        String sql = "SELECT * FROM empresa ORDER BY razon_social";
        List<Empresa> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public boolean actualizar(Empresa e) throws SQLException {
        String sql = "UPDATE empresa SET ruc = ?, razon_social = ?, contacto = ?, telefono = ? "
                   + "WHERE id_empresa = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.getRuc());
            ps.setString(2, e.getRazonSocial());
            ps.setString(3, e.getContacto());
            ps.setString(4, e.getTelefono());
            ps.setInt(5, e.getIdEmpresa());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM empresa WHERE id_empresa = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Empresa mapear(ResultSet rs) throws SQLException {
        Empresa e = new Empresa();
        e.setIdEmpresa(rs.getInt("id_empresa"));
        e.setRuc(rs.getString("ruc"));
        e.setRazonSocial(rs.getString("razon_social"));
        e.setContacto(rs.getString("contacto"));
        e.setTelefono(rs.getString("telefono"));
        return e;
    }
}
