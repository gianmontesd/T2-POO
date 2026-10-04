package pe.pcsac.vista;

import pe.pcsac.dao.AlumnoDAO;
import pe.pcsac.dao.CapacitacionDAO;
import pe.pcsac.modelo.Alumno;
import pe.pcsac.modelo.Capacitacion;
import pe.pcsac.modelo.Inscripcion;
import pe.pcsac.servicio.ImportadorNotasCsv;
import pe.pcsac.servicio.InscripcionServicio;
import pe.pcsac.servicio.ServicioException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

/** Ventana 4: inscripcion de alumnos y registro de notas (RF-04 a RF-07). */
public class VentanaInscripciones extends JFrame {

    private final InscripcionServicio servicio = new InscripcionServicio();
    private final ImportadorNotasCsv importador = new ImportadorNotasCsv();
    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private final CapacitacionDAO capacitacionDAO = new CapacitacionDAO();

    private final JComboBox<Capacitacion> cboCapacitacion = new JComboBox<>();
    private final JComboBox<Alumno> cboAlumno = new JComboBox<>();
    private final JTextField txtVoucher = new JTextField();
    private final JTextField txtNota = new JTextField();
    private final DefaultTableModel modelo =
            Ui.modeloSoloLectura("ID", "DNI", "Alumno", "Empresa", "Voucher", "Nota", "Estado");
    private final JTable tabla = new JTable(modelo);

    public VentanaInscripciones() {
        setTitle("Inscripciones y notas");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(980, 560);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Ui.FONDO);
        raiz.add(Ui.cabecera("Inscripciones y registro de notas",
                "RF-04 y RF-05 inscripcion sin duplicados  ·  RF-06 y RF-07 nota y estado automatico"),
                BorderLayout.NORTH);

        JPanel form = Ui.formulario(
                new JLabel("Capacitacion"), cboCapacitacion, new JLabel("Alumno"), cboAlumno,
                new JLabel("Voucher de pago"), txtVoucher, new JLabel("Nota (0-20)"), txtNota);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.setBackground(Ui.FONDO);
        JButton bInscribir = Ui.boton("Inscribir alumno");
        JButton bNota = Ui.boton("Registrar nota a la fila seleccionada");
        JButton bCsv = Ui.boton("Importar notas desde CSV");
        botones.add(bInscribir); botones.add(bNota); botones.add(bCsv);

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(Ui.FONDO);
        norte.add(form, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(norte, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);
        setContentPane(raiz);

        bInscribir.addActionListener(e -> inscribir());
        bNota.addActionListener(e -> registrarNota());
        bCsv.addActionListener(e -> importarCsv());
        cboCapacitacion.addActionListener(e -> refrescar());

        cargarCombos();
        refrescar();
    }

    private void cargarCombos() {
        try {
            cboCapacitacion.removeAllItems();
            for (Capacitacion c : capacitacionDAO.listar()) cboCapacitacion.addItem(c);
            cboAlumno.removeAllItems();
            for (Alumno a : alumnoDAO.listar()) cboAlumno.addItem(a);
        } catch (SQLException ex) {
            Ui.error(this, "No se pudieron cargar los catalogos: " + ex.getMessage());
        }
    }

    private Capacitacion capacitacionActual() {
        return (Capacitacion) cboCapacitacion.getSelectedItem();
    }

    private void refrescar() {
        Capacitacion c = capacitacionActual();
        if (c == null) return;
        try {
            modelo.setRowCount(0);
            List<Inscripcion> lista = servicio.listarPorCapacitacion(c.getIdCapacitacion());
            for (Inscripcion i : lista) {
                modelo.addRow(new Object[]{i.getIdInscripcion(), i.getAlumno().getDni(),
                        i.getAlumno().getNombreCompleto(), i.getAlumno().getEmpresa().getRazonSocial(),
                        i.getVoucher() == null ? "" : i.getVoucher(),
                        i.getNotaFinal() == null ? "" : String.format("%.2f", i.getNotaFinal()),
                        i.getEstado().getDescripcion()});
            }
            setTitle("Inscripciones  ·  " + c.getCodigo() + "  (" + lista.size() + " alumnos)");
        } catch (ServicioException ex) {
            Ui.error(this, ex.getMessage());
        }
    }

    private void inscribir() {
        Capacitacion c = capacitacionActual();
        Alumno a = (Alumno) cboAlumno.getSelectedItem();
        if (c == null || a == null) { Ui.error(this, "Seleccione capacitacion y alumno."); return; }
        try {
            servicio.inscribir(a.getIdAlumno(), c.getIdCapacitacion(), txtVoucher.getText().trim());
            Ui.info(this, "Alumno inscrito en " + c.getCodigo() + ".");
            txtVoucher.setText("");
            refrescar();
        } catch (ServicioException ex) {
            Ui.error(this, ex.getMessage());
        }
    }

    private void registrarNota() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { Ui.error(this, "Seleccione una fila de la tabla."); return; }
        try {
            double nota = Double.parseDouble(txtNota.getText().trim().replace(",", "."));
            int id = (int) modelo.getValueAt(fila, 0);
            Inscripcion i = servicio.registrarNota(id, nota);
            Ui.info(this, i.getAlumno().getNombreCompleto() + " queda como " + i.getEstado() + ".");
            txtNota.setText("");
            refrescar();
        } catch (NumberFormatException ex) {
            Ui.error(this, "Escriba la nota en el campo 'Nota (0-20)'.");
        } catch (ServicioException ex) {
            Ui.error(this, ex.getMessage());
        }
    }

    private void importarCsv() {
        Capacitacion c = capacitacionActual();
        if (c == null) { Ui.error(this, "Seleccione una capacitacion."); return; }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccione el CSV de notas (dni;nota)");
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            Path archivo = fc.getSelectedFile().toPath();
            ImportadorNotasCsv.Resultado r = importador.importar(archivo, c.getIdCapacitacion());
            StringBuilder msg = new StringBuilder(r.resumen());
            for (String e : r.getErrores()) msg.append("\n· ").append(e);
            Ui.info(this, msg.toString());
            refrescar();
        } catch (ServicioException ex) {
            Ui.error(this, ex.getMessage());
        }
    }
}
