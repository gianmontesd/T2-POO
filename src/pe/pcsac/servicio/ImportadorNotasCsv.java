package pe.pcsac.servicio;

import pe.pcsac.modelo.Inscripcion;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * RF-06: carga masiva de notas desde un CSV con formato "dni;nota".
 * Devuelve un resumen con lo aplicado y lo rechazado, sin detener el proceso
 * en la primera fila con problemas.
 */
public class ImportadorNotasCsv {

    private final InscripcionServicio servicio = new InscripcionServicio();

    public static class Resultado {
        private int aplicadas;
        private final List<String> errores = new ArrayList<>();

        public int getAplicadas() { return aplicadas; }

        public List<String> getErrores() { return errores; }

        public String resumen() {
            return "Notas aplicadas: " + aplicadas + ". Filas con error: " + errores.size() + ".";
        }
    }

    public Resultado importar(Path archivo, int idCapacitacion) throws ServicioException {
        Resultado r = new Resultado();
        try (BufferedReader br = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            int nro = 0;
            while ((linea = br.readLine()) != null) {
                nro++;
                if (linea.isBlank()) continue;
                if (nro == 1 && linea.toLowerCase().startsWith("dni")) continue;   // cabecera
                String[] campos = linea.split("[;,]");
                if (campos.length < 2) {
                    r.errores.add("Linea " + nro + ": se esperaban 2 columnas (dni;nota).");
                    continue;
                }
                String dni = campos[0].trim();
                try {
                    double nota = Double.parseDouble(campos[1].trim().replace(",", "."));
                    Inscripcion i = servicio.buscarPorDniYCapacitacion(dni, idCapacitacion);
                    if (i == null) {
                        r.errores.add("Linea " + nro + ": el DNI " + dni
                                + " no esta inscrito en esta capacitacion.");
                        continue;
                    }
                    servicio.registrarNota(i.getIdInscripcion(), nota);
                    r.aplicadas++;
                } catch (NumberFormatException e) {
                    r.errores.add("Linea " + nro + ": la nota '" + campos[1].trim() + "' no es numerica.");
                } catch (ServicioException e) {
                    r.errores.add("Linea " + nro + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new ServicioException("No se pudo leer el archivo: " + e.getMessage(), e);
        }
        return r;
    }
}
