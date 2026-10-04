package pe.pcsac.vista;

import pe.pcsac.dao.AlumnoDAO;
import pe.pcsac.dao.EmpresaDAO;
import pe.pcsac.modelo.Alumno;
import pe.pcsac.modelo.Empresa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/** Ventana 2: alta, edicion y consulta de alumnos (RF-01, RF-03). */
public class VentanaAlumnos extends JFrame {

    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private final EmpresaDAO empresaDAO = new EmpresaDAO();

    private final JTextField txtDni = new JTextField();
    private final JTextField txtNombres = new JTextField();
    private final JTextField txtApellidos = new JTextField();
    private final JTextField txtCorreo = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JTextField txtArea = new JTextField();
    private final JComboBox<Empresa> cboEmpresa = new JComboBox<>();
    private final DefaultTableModel modelo =
            Ui.modeloSoloLectura("ID", "DNI", "Apellidos", "Nombres", "Area", "Empresa", "Correo");
    private final JTable tabla = new JTable(modelo);

    private int idSeleccionado = 0;

    public VentanaAlumnos() {
        setTitle("Alumnos");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Ui.FONDO);
        raiz.add(Ui.cabecera("Registro de alumnos", "RF-01  ·  trabajadores de las empresas mineras"),
                BorderLayout.NORTH);

        JPanel form = Ui.formulario(
                new JLabel("DNI"), txtDni, new JLabel("Nombres"), txtNombres,
                new JLabel("Apellidos"), txtApellidos, new JLabel("Correo"), txtCorreo,
                new JLabel("Telefono"), txtTelefono, new JLabel("Area"), txtArea,
                new JLabel("Empresa"), cboEmpresa, new JLabel(""), new JLabel(""));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.setBackground(Ui.FONDO);
        JButton bNuevo = Ui.boton("Nuevo");
        JButton bGuardar = Ui.boton("Guardar");
        JButton bEliminar = Ui.boton("Eliminar");
        botones.add(bNuevo); botones.add(bGuardar); botones.add(bEliminar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);
        norte.setBackground(Ui.FONDO);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(norte, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);
        setContentPane(raiz);

        bNuevo.addActionListener(e -> limpiar());
        bGuardar.addActionListener(e -> guardar());
        bEliminar.addActionListener(e -> eliminar());
        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());

        cargarEmpresas();
        refrescar();
    }

    private void cargarEmpresas() {
        try {
            cboEmpresa.removeAllItems();
            for (Empresa e : empresaDAO.listar()) cboEmpresa.addItem(e);
        } catch (SQLException ex) {
            Ui.error(this, "No se pudieron cargar las empresas: " + ex.getMessage());
        }
    }

    private void refrescar() {
        try {
            modelo.setRowCount(0);
            List<Alumno> lista = alumnoDAO.listar();
            for (Alumno a : lista) {
                modelo.addRow(new Object[]{a.getIdAlumno(), a.getDni(), a.getApellidos(),
                        a.getNombres(), a.getArea(), a.getEmpresa().getRazonSocial(), a.getCorreo()});
            }
            setTitle("Alumnos  (" + lista.size() + " registrados)");
        } catch (SQLException ex) {
            Ui.error(this, "No se pudo listar: " + ex.getMessage());
        }
    }

    private void guardar() {
        try {
            Empresa emp = (Empresa) cboEmpresa.getSelectedItem();
            if (emp == null) { Ui.error(this, "Seleccione una empresa."); return; }
            Alumno a = new Alumno(txtDni.getText().trim(), txtNombres.getText(), txtApellidos.getText(),
                    txtCorreo.getText(), txtTelefono.getText(), txtArea.getText(), emp);
            if (idSeleccionado == 0) {
                alumnoDAO.insertar(a);
                Ui.info(this, "Alumno registrado correctamente.");
            } else {
                a.setIdAlumno(idSeleccionado);
                alumnoDAO.actualizar(a);
                Ui.info(this, "Alumno actualizado.");
            }
            limpiar();
            refrescar();
        } catch (IllegalArgumentException ex) {
            Ui.error(this, ex.getMessage());
        } catch (SQLException ex) {
            Ui.error(this, ex.getMessage().contains("Duplicate")
                    ? "Ya existe un alumno con ese DNI." : ex.getMessage());
        }
    }

    private void eliminar() {
        if (idSeleccionado == 0) { Ui.error(this, "Seleccione un alumno de la tabla."); return; }
        try {
            alumnoDAO.eliminar(idSeleccionado);
            limpiar();
            refrescar();
        } catch (SQLException ex) {
            Ui.error(this, "No se puede eliminar: el alumno tiene inscripciones registradas.");
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        idSeleccionado = (int) modelo.getValueAt(fila, 0);
        txtDni.setText((String) modelo.getValueAt(fila, 1));
        txtApellidos.setText((String) modelo.getValueAt(fila, 2));
        txtNombres.setText((String) modelo.getValueAt(fila, 3));
        txtArea.setText(valor(modelo.getValueAt(fila, 4)));
        txtCorreo.setText(valor(modelo.getValueAt(fila, 6)));
        for (int i = 0; i < cboEmpresa.getItemCount(); i++) {
            if (cboEmpresa.getItemAt(i).getRazonSocial().equals(modelo.getValueAt(fila, 5))) {
                cboEmpresa.setSelectedIndex(i);
                break;
            }
        }
    }

    private String valor(Object o) { return o == null ? "" : o.toString(); }

    private void limpiar() {
        idSeleccionado = 0;
        txtDni.setText(""); txtNombres.setText(""); txtApellidos.setText("");
        txtCorreo.setText(""); txtTelefono.setText(""); txtArea.setText("");
        tabla.clearSelection();
    }
}
