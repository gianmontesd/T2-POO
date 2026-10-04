package pe.pcsac.vista;

import pe.pcsac.dao.ConexionDB;

import javax.swing.*;
import java.awt.*;

/** Ventana 1: menu principal del sistema. */
public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        setTitle("Peru Consulting SAC - Administracion de Certificados");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 430);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Ui.FONDO);
        raiz.add(Ui.cabecera("Administracion de Certificados",
                "Peru Consulting SAC  ·  Seguridad y Salud Ocupacional"), BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 2, 14, 14));
        centro.setBorder(BorderFactory.createEmptyBorder(24, 24, 14, 24));
        centro.setBackground(Ui.FONDO);
        centro.add(tarjeta("Alumnos y empresas", "RF-01, RF-03",
                e -> new VentanaAlumnos().setVisible(true)));
        centro.add(tarjeta("Cursos y capacitaciones", "RF-02",
                e -> new VentanaCapacitaciones().setVisible(true)));
        centro.add(tarjeta("Inscripciones y notas", "RF-04 a RF-07",
                e -> new VentanaInscripciones().setVisible(true)));
        centro.add(tarjeta("Certificados", "RF-08, RF-09, RF-10",
                e -> new VentanaCertificados().setVisible(true)));
        raiz.add(centro, BorderLayout.CENTER);

        JLabel pie = new JLabel("  Base de datos: " + ConexionDB.getUrl().replaceAll("\\?.*", ""));
        pie.setFont(pie.getFont().deriveFont(10f));
        pie.setForeground(Color.GRAY);
        pie.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));
        raiz.add(pie, BorderLayout.SOUTH);
        setContentPane(raiz);
    }

    private JButton tarjeta(String titulo, String rf, java.awt.event.ActionListener accion) {
        JButton b = new JButton("<html><div style='text-align:center'>"
                + "<span style='font-size:13pt'><b>" + titulo + "</b></span><br>"
                + "<span style='font-size:9pt;color:#777'>" + rf + "</span></div></html>");
        b.setFocusPainted(false);
        b.setBackground(Color.WHITE);
        b.setBorder(BorderFactory.createLineBorder(new Color(0xD0D0C8)));
        b.addActionListener(accion);
        return b;
    }
}
