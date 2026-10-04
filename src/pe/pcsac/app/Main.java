package pe.pcsac.app;

import pe.pcsac.dao.ConexionDB;
import pe.pcsac.vista.VentanaPrincipal;

import javax.swing.*;

/**
 * Punto de entrada del sistema de Administracion de Certificados.
 *
 *   java -cp ... pe.pcsac.app.Main          abre la interfaz grafica
 *   java -cp ... pe.pcsac.app.Main demo     ejecuta el escenario de demostracion
 */
public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("demo")) {
            DemoRequerimientos.ejecutar();
            return;
        }
        if (!ConexionDB.probar()) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar con MySQL.\n\n"
                  + "Verifique que el servicio este activo y que se haya ejecutado\n"
                  + "el script sql/01_esquema.sql.\n\nURL: " + ConexionDB.getUrl(),
                    "Error de conexion", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorado) {
                // se usa el aspecto por defecto
            }
            new VentanaPrincipal().setVisible(true);
        });
    }
}
