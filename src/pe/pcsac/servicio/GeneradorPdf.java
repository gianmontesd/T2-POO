package pe.pcsac.servicio;

import pe.pcsac.modelo.Certificado;
import pe.pcsac.modelo.Inscripcion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Escribe el certificado como un PDF valido (una pagina A4 horizontal) sin
 * librerias externas: compone directamente los objetos del formato PDF 1.4.
 */
public class GeneradorPdf {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final float ANCHO = 842f;   // A4 apaisado, en puntos
    private static final float ALTO  = 595f;

    public Path generar(Certificado cert, Path carpeta) throws ServicioException {
        try {
            Files.createDirectories(carpeta);
            Path destino = carpeta.resolve(cert.getCodigo() + ".pdf");
            Files.write(destino, construir(cert));
            return destino;
        } catch (IOException e) {
            throw new ServicioException("No se pudo escribir el certificado: " + e.getMessage(), e);
        }
    }

    private byte[] construir(Certificado cert) throws IOException {
        Inscripcion i = cert.getInscripcion();
        StringBuilder t = new StringBuilder();
        t.append("BT\n");
        centrado(t, 26, 470, "PERU CONSULTING SAC", true);
        centrado(t, 14, 440, "Seguridad y Salud Ocupacional", false);
        centrado(t, 32, 380, "CERTIFICADO", true);
        centrado(t, 13, 335, "Se otorga el presente certificado a", false);
        centrado(t, 24, 295, i.getAlumno().getNombreCompleto().toUpperCase(), true);
        centrado(t, 12, 270, "DNI " + i.getAlumno().getDni()
                + "   -   " + i.getAlumno().getEmpresa().getRazonSocial(), false);
        centrado(t, 13, 230, "por haber aprobado satisfactoriamente la capacitacion", false);
        centrado(t, 18, 200, i.getCapacitacion().getCurso().getNombre(), true);
        centrado(t, 12, 172, "Duracion: " + i.getCapacitacion().getCurso().getDuracionHoras()
                + " horas    Nota final: " + String.format("%.2f", i.getNotaFinal())
                + "    Fecha: " + i.getCapacitacion().getFecha().format(FECHA), false);

        linea(t, 150, 110, 370, 110);
        texto(t, 11, 150, 95, i.getCapacitacion().getInstructor().getNombreCompleto(), true);
        texto(t, 9, 150, 82, i.getCapacitacion().getInstructor().getEspecialidad(), false);
        texto(t, 9, 150, 70, "Instructor responsable", false);

        texto(t, 9, 520, 95, "Codigo: " + cert.getCodigo(), false);
        texto(t, 9, 520, 82, "Emitido: " + cert.getFechaEmision().format(FECHA), false);
        texto(t, 9, 520, 70, "Capacitacion: " + i.getCapacitacion().getCodigo(), false);
        t.append("ET\n");

        byte[] flujo = t.toString().getBytes(StandardCharsets.ISO_8859_1);

        List<byte[]> objetos = new ArrayList<>();
        objetos.add(obj("<< /Type /Catalog /Pages 2 0 R >>"));
        objetos.add(obj("<< /Type /Pages /Kids [3 0 R] /Count 1 >>"));
        objetos.add(obj("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + (int) ANCHO + " " + (int) ALTO + "]"
                + " /Resources << /Font << /F1 5 0 R /F2 6 0 R >> >> /Contents 4 0 R >>"));
        objetos.add(flujoContenido(flujo));
        objetos.add(obj("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>"));
        objetos.add(obj("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write("%PDF-1.4\n".getBytes(StandardCharsets.ISO_8859_1));
        int[] offsets = new int[objetos.size() + 1];
        for (int n = 0; n < objetos.size(); n++) {
            offsets[n + 1] = out.size();
            out.write((n + 1 + " 0 obj\n").getBytes(StandardCharsets.ISO_8859_1));
            out.write(objetos.get(n));
            out.write("\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));
        }
        int inicioXref = out.size();
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n0 ").append(objetos.size() + 1).append("\n");
        xref.append("0000000000 65535 f \n");
        for (int n = 1; n <= objetos.size(); n++) {
            xref.append(String.format("%010d 00000 n \n", offsets[n]));
        }
        xref.append("trailer\n<< /Size ").append(objetos.size() + 1).append(" /Root 1 0 R >>\n")
            .append("startxref\n").append(inicioXref).append("\n%%EOF");
        out.write(xref.toString().getBytes(StandardCharsets.ISO_8859_1));
        return out.toByteArray();
    }

    private byte[] obj(String cuerpo) {
        return cuerpo.getBytes(StandardCharsets.ISO_8859_1);
    }

    private byte[] flujoContenido(byte[] flujo) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        b.write(("<< /Length " + flujo.length + " >>\nstream\n").getBytes(StandardCharsets.ISO_8859_1));
        b.write(flujo);
        b.write("\nendstream".getBytes(StandardCharsets.ISO_8859_1));
        return b.toByteArray();
    }

    private void texto(StringBuilder t, int tam, float x, float y, String txt, boolean negrita) {
        t.append("/").append(negrita ? "F2" : "F1").append(" ").append(tam).append(" Tf\n")
         .append("1 0 0 1 ").append(fmt(x)).append(" ").append(fmt(y)).append(" Tm\n")
         .append("(").append(escapar(txt)).append(") Tj\n");
    }

    private void centrado(StringBuilder t, int tam, float y, String txt, boolean negrita) {
        float ancho = txt.length() * tam * (negrita ? 0.58f : 0.52f);
        texto(t, tam, Math.max(40, (ANCHO - ancho) / 2), y, txt, negrita);
    }

    private void linea(StringBuilder t, float x1, float y1, float x2, float y2) {
        t.append("ET\n0.5 w\n").append(fmt(x1)).append(" ").append(fmt(y1)).append(" m ")
         .append(fmt(x2)).append(" ").append(fmt(y2)).append(" l S\nBT\n");
    }

    private String fmt(float v) { return String.format("%.2f", v); }

    /** Sustituye los acentos y escapa los caracteres reservados del formato PDF. */
    private String escapar(String s) {
        String base = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return base.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}
