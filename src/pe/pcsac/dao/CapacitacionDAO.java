package pe.pcsac.dao;

import pe.pcsac.modelo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Operaciones CRUD de la tabla capacitacion. */
public class CapacitacionDAO {

    private static final String SELECT_BASE =
            "SELECT cap.*, "
          + "       cu.codigo AS cu_codigo, cu.nombre AS cu_nombre, cu.duracion_horas, cu.nota_minima AS cu_nota, "
          + "       em.ruc, em.razon_social, em.contacto, em.telefono AS em_telefono, "
          + "       ins.dni AS ins_dni, ins.nombres AS ins_nombres, ins.apellidos AS ins_apellidos, "
          + "       ins.especialidad "
          + "FROM capacitacion cap "
          + "INNER JOIN curso      cu  ON cu.id_curso = cap.id_curso "
          + "INNER JOIN empresa    em  ON em.id_empresa = cap.id_empresa "
          + "INNER JOIN instructor ins ON ins.id_instructor = cap.id_instructor";

    public int insertar(Capacitacion c) throws SQLException {
        String sql = "INSERT INTO capacitacion (codigo, id_curso, id_empresa, id_instructor, fecha, nota_minima) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCodigo());
            ps.setInt(2, c.getCurso().getIdCurso());
            ps.setInt(3, c.getEmpresa().getIdEmpresa());
            ps.setInt(4, c.getInstructor().getIdInstructor());
            ps.setDate(5, Date.valueOf(c.getFecha()));
            ps.setDouble(6, c.getNotaMinima());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setIdCapacitacion(rs.getInt(1));
            }
            return c.getIdCapacitacion();
        }
    }

    public Capacitacion buscarPorId(int id) throws SQLException {
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(SELECT_BASE + " WHERE cap.id_capacitacion = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public List<Capacitacion> listar() throws SQLException {
        List<Capacitacion> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(SELECT_BASE + " ORDER BY cap.fecha DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public boolean actualizar(Capacitacion c) throws SQLException {
        String sql = "UPDATE capacitacion SET codigo = ?, id_curso = ?, id_empresa = ?, "
                   + "id_instructor = ?, fecha = ?, nota_minima = ? WHERE id_capacitacion = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getCodigo());
            ps.setInt(2, c.getCurso().getIdCurso());
            ps.setInt(3, c.getEmpresa().getIdEmpresa());
            ps.setInt(4, c.getInstructor().getIdInstructor());
            ps.setDate(5, Date.valueOf(c.getFecha()));
            ps.setDouble(6, c.getNotaMinima());
            ps.setInt(7, c.getIdCapacitacion());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM capacitacion WHERE id_capacitacion = ?";
        try (Connection cn = ConexionDB.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    static Capacitacion mapear(ResultSet rs) throws SQLException {
        Curso cu = new Curso();
        cu.setIdCurso(rs.getInt("id_curso"));
        cu.setCodigo(rs.getString("cu_codigo"));
        cu.setNombre(rs.getString("cu_nombre"));
        cu.setDuracionHoras(rs.getInt("duracion_horas"));
        cu.setNotaMinima(rs.getDouble("cu_nota"));

        Empresa em = new Empresa();
        em.setIdEmpresa(rs.getInt("id_empresa"));
        em.setRuc(rs.getString("ruc"));
        em.setRazonSocial(rs.getString("razon_social"));
        em.setContacto(rs.getString("contacto"));
        em.setTelefono(rs.getString("em_telefono"));

        Instructor ins = new Instructor();
        ins.setIdInstructor(rs.getInt("id_instructor"));
        ins.setDni(rs.getString("ins_dni"));
        ins.setNombres(rs.getString("ins_nombres"));
        ins.setApellidos(rs.getString("ins_apellidos"));
        ins.setEspecialidad(rs.getString("especialidad"));

        Capacitacion c = new Capacitacion();
        c.setIdCapacitacion(rs.getInt("id_capacitacion"));
        c.setCodigo(rs.getString("codigo"));
        c.setCurso(cu);
        c.setEmpresa(em);
        c.setInstructor(ins);
        c.setFecha(rs.getDate("fecha").toLocalDate());
        c.setNotaMinima(rs.getDouble("nota_minima"));
        return c;
    }
}
