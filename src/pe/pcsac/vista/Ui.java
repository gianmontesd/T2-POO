package pe.pcsac.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Utilidades compartidas por las ventanas. No contiene logica de negocio. */
public final class Ui {

    public static final Color AZUL = new Color(0x1F3864);
    public static final Color NARANJA = new Color(0xE8A020);
    public static final Color FONDO = new Color(0xF5F5F0);

    private Ui() { }

    public static DefaultTableModel modeloSoloLectura(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
    }

    public static JPanel cabecera(String titulo, String subtitulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AZUL);
        p.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel t = new JLabel(titulo);
        t.setForeground(Color.WHITE);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 17f));
        JLabel s = new JLabel(subtitulo);
        s.setForeground(new Color(0xD6DCE8));
        s.setFont(s.getFont().deriveFont(11f));
        p.add(t, BorderLayout.NORTH);
        p.add(s, BorderLayout.SOUTH);
        return p;
    }

    public static JButton boton(String texto) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        return b;
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Atencion", JOptionPane.WARNING_MESSAGE);
    }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Informacion", JOptionPane.INFORMATION_MESSAGE);
    }

    public static JPanel formulario(JComponent... campos) {
        JPanel p = new JPanel(new GridLayout(0, 4, 8, 6));
        p.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        p.setBackground(FONDO);
        for (JComponent c : campos) p.add(c);
        return p;
    }
}
