package pe.pcsac.vista;

import pe.pcsac.dao.CapacitacionDAO;
import pe.pcsac.dao.EmpresaDAO;
import pe.pcsac.modelo.Capacitacion;
import pe.pcsac.modelo.Certificado;
import pe.pcsac.modelo.Empresa;
import pe.pcsac.modelo.Inscripcion;
import pe.pcsac.servicio.CertificadoServicio;
import pe.pcsac.servicio.InscripcionServicio;
import pe.pcsac.servicio.ServicioException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Ventana 5: emision, consulta y descarga de certificados (RF-08, RF-09, RF-10). */
public class VentanaCertificados extends JFrame {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CertificadoServicio servicio = new CertificadoServicio();
    private final InscripcionServicio inscripcionServicio = new InscripcionServicio();
    private final CapacitacionDAO capacitacionDAO = new CapacitacionDAO();
    private final EmpresaDAO empresaDAO = new EmpresaDAO();

    private final JComboBox<Capacitacion> cboCapacitacion = new JComboBox<>();
    private final JComboBox<Empresa> cboEmpresa = new JComboBox<>();
    private final DefaultTableModel modelo =
            Ui.modeloSoloLectura("Codigo", "Alumno", "DNI", "Empresa", "Curso", "Nota", "Emitido", "Archivo");
    private final JTable tabla = new JTable(modelo);

    public VentanaCertificados() {
        setTitle("Certificados");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1020, 540);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Ui.FONDO);
        raiz.add(Ui.cabecera("Emision y consulta de certificados",
                "RF-09 emision en PDF  ·  RF-08 consulta  ·  RF-10 filtro por empresa minera"),
                BorderLayout.NORTH);

        JPanel form = Ui.formulario(
                new JLabel("Capacitacion"), cboCapacitacion,
                new JLabel("Filtrar por empresa"), cboEmpresa);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.setBackground(Ui.FONDO);
        JButton bLote = Ui.boton("Emitir a todos los aprobados");
        JButton bTodos = Ui.boton("Ver todos");
        JButton bFiltrar = Ui.boton("Ver solo de la empresa");
        JButton bAbrir = Ui.boton("Abrir PDF seleccionado");
        botones.add(bLote); botones.add(bTodos); botones.add(bFiltrar); botones.add(bAbrir);

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(Ui.FONDO);
        norte.add(form, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(norte, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);
        setContentPane(raiz);

        bLote.addActionListener(e -> emitirLote());
        bTodos.addActionListener(e -> listarTodos());
        bFiltrar.addActionListener(e -> listarPorEmpresa());
        bAbrir.addActionListener(e -> abrirPdf());

        cargarCombos();
        listarTodos();
    }

    private void cargarCombos() {
        try {
            cboCapacitacion.removeAllItems();
            for (Capacitacion c : capacitacionDAO.listar()) cboCapacitacion.addItem(c);
            cboEmpresa.removeAllItems();
            for (Empresa e : empresaDAO.listar()) cboEmpresa.addItem(e);
        } catch (SQLException ex) {
            Ui.error(this, "No se pudieron cargar los catalogos: " + ex.getMessage());
        }
    }

    private void pintar(List<Certificado> lista, String titulo) {
        modelo.setRowCount(0);
        for (Certificado c : lista) {
            Inscripcion i = c.getInscripcion();
            modelo.addRow(new Object[]{c.getCodigo(), i.getAlumno().getNombreCompleto(),
                    i.getAlumno().getDni(), i.getAlumno().getEmpresa().getRazonSocial(),
                    i.getCapacitacion().getCurso().getNombre(),
                    String.format("%.2f", i.getNotaFinal()),
                    c.getFechaEmision().format(FECHA),
                    c.getRutaArchivo() == null ? "" : new File(c.getRutaArchivo()).getName()});
        }
        setTitle("Certificados  ·  " + titulo + "  (" + lista.size() + ")");
    }

    private void listarTodos() {
        try {
            pintar(servicio.listar(), "todos");
        } catch (ServicioException ex) {
            Ui.error(this, ex.getMessage());
        }
    }

    private void listarPorEmpresa() {
        Empresa e = (Empresa) cboEmpresa.getSelectedItem();
        if (e == null) { Ui.error(this, "Seleccione una empresa."); return; }
        try {
            pintar(servicio.listarPorEmpresa(e.getIdEmpresa()), e.getRazonSocial());
        } catch (ServicioException ex) {
            Ui.error(this, ex.getMessage());
        }
    }

    private void emitirLote() {
        Capacitacion c = (Capacitacion) cboCapacitacion.getSelectedItem();
        if (c == null) { Ui.error(this, "Seleccione una capacitacion."); return; }
        try {
            List<Certificado> emitidos = servicio.emitirLote(c.getIdCapacitacion());
            int aprobados = 0;
            for (Inscripcion i : inscripcionServicio.listarPorCapacitacion(c.getIdCapacitacion())) {
                if (i.puedeCertificarse()) aprobados++;
            }
            Ui.info(this, "Certificados emitidos ahora: " + emitidos.size()
                    + "\nAlumnos aprobados en " + c.getCodigo() + ": " + aprobados
                    + "\nLos PDF se guardan en la carpeta 'certificados'.");
            listarTodos();
        } catch (ServicioException ex) {
            Ui.error(this, ex.getMessage());
        }
    }

    private void abrirPdf() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { Ui.error(this, "Seleccione un certificado."); return; }
        try {
            Certificado c = servicio.listar().stream()
                    .filter(x -> x.getCodigo().equals(modelo.getValueAt(fila, 0)))
                    .findFirst().orElse(null);
            if (c == null || c.getRutaArchivo() == null) {
                Ui.error(this, "El certificado no tiene archivo asociado.");
                return;
            }
            File f = new File(c.getRutaArchivo());
            if (!f.exists()) { Ui.error(this, "No se encontro el archivo: " + f); return; }
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(f);
            else Ui.info(this, "Archivo en: " + f.getAbsolutePath());
        } catch (Exception ex) {
            Ui.error(this, "No se pudo abrir el PDF: " + ex.getMessage());
        }
    }
}
