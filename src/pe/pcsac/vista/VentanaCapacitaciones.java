package pe.pcsac.vista;

import pe.pcsac.dao.*;
import pe.pcsac.modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Ventana 3: programacion de capacitaciones a partir del catalogo de cursos (RF-02). */
public class VentanaCapacitaciones extends JFrame {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CapacitacionDAO capacitacionDAO = new CapacitacionDAO();
    private final CursoDAO cursoDAO = new CursoDAO();
    private final EmpresaDAO empresaDAO = new EmpresaDAO();
    private final InstructorDAO instructorDAO = new InstructorDAO();

    private final JTextField txtCodigo = new JTextField();
    private final JComboBox<Curso> cboCurso = new JComboBox<>();
    private final JComboBox<Empresa> cboEmpresa = new JComboBox<>();
    private final JComboBox<Instructor> cboInstructor = new JComboBox<>();
    private final JTextField txtFecha = new JTextField(LocalDate.now().format(FECHA));
    private final JTextField txtNota = new JTextField("13.0");
    private final DefaultTableModel modelo =
            Ui.modeloSoloLectura("ID", "Codigo", "Curso", "Empresa", "Instructor", "Fecha", "Nota min.");
    private final JTable tabla = new JTable(modelo);

    public VentanaCapacitaciones() {
        setTitle("Capacitaciones");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(960, 500);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Ui.FONDO);
        raiz.add(Ui.cabecera("Programacion de capacitaciones",
                "RF-02  ·  curso, empresa, instructor, fecha y nota minima"), BorderLayout.NORTH);

        JPanel form = Ui.formulario(
                new JLabel("Codigo"), txtCodigo, new JLabel("Curso"), cboCurso,
                new JLabel("Empresa"), cboEmpresa, new JLabel("Instructor"), cboInstructor,
                new JLabel("Fecha (dd/MM/yyyy)"), txtFecha, new JLabel("Nota minima"), txtNota);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.setBackground(Ui.FONDO);
        JButton bGuardar = Ui.boton("Registrar capacitacion");
        JButton bRefrescar = Ui.boton("Refrescar");
        botones.add(bGuardar); botones.add(bRefrescar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(Ui.FONDO);
        norte.add(form, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(norte, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);
        setContentPane(raiz);

        bGuardar.addActionListener(e -> guardar());
        bRefrescar.addActionListener(e -> refrescar());

        cargarCombos();
        refrescar();
    }

    private void cargarCombos() {
        try {
            cboCurso.removeAllItems();
            for (Curso c : cursoDAO.listar()) cboCurso.addItem(c);
            cboEmpresa.removeAllItems();
            for (Empresa e : empresaDAO.listar()) cboEmpresa.addItem(e);
            cboInstructor.removeAllItems();
            for (Instructor i : instructorDAO.listar()) cboInstructor.addItem(i);
        } catch (SQLException ex) {
            Ui.error(this, "No se pudieron cargar los catalogos: " + ex.getMessage());
        }
    }

    private void refrescar() {
        try {
            modelo.setRowCount(0);
            List<Capacitacion> lista = capacitacionDAO.listar();
            for (Capacitacion c : lista) {
                modelo.addRow(new Object[]{c.getIdCapacitacion(), c.getCodigo(),
                        c.getCurso().getNombre(), c.getEmpresa().getRazonSocial(),
                        c.getInstructor().getNombreCompleto(), c.getFecha().format(FECHA),
                        String.format("%.2f", c.getNotaMinima())});
            }
            setTitle("Capacitaciones  (" + lista.size() + " programadas)");
        } catch (SQLException ex) {
            Ui.error(this, "No se pudo listar: " + ex.getMessage());
        }
    }

    private void guardar() {
        try {
            Curso cu = (Curso) cboCurso.getSelectedItem();
            Empresa em = (Empresa) cboEmpresa.getSelectedItem();
            Instructor in = (Instructor) cboInstructor.getSelectedItem();
            if (cu == null || em == null || in == null) {
                Ui.error(this, "Seleccione curso, empresa e instructor.");
                return;
            }
            Capacitacion c = new Capacitacion(txtCodigo.getText().trim(), cu, em, in,
                    LocalDate.parse(txtFecha.getText().trim(), FECHA),
                    Double.parseDouble(txtNota.getText().trim().replace(",", ".")));
            capacitacionDAO.insertar(c);
            Ui.info(this, "Capacitacion " + c.getCodigo() + " registrada.");
            txtCodigo.setText("");
            refrescar();
        } catch (java.time.format.DateTimeParseException ex) {
            Ui.error(this, "La fecha debe tener el formato dd/MM/yyyy.");
        } catch (NumberFormatException ex) {
            Ui.error(this, "La nota minima debe ser numerica.");
        } catch (IllegalArgumentException ex) {
            Ui.error(this, ex.getMessage());
        } catch (SQLException ex) {
            Ui.error(this, ex.getMessage().contains("Duplicate")
                    ? "Ya existe una capacitacion con ese codigo." : ex.getMessage());
        }
    }
}
