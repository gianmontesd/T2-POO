package pe.pcsac.modelo;

import java.time.LocalDate;
import java.util.Objects;

/** Constancia emitida a un alumno aprobado (RF-08, RF-09, RF-10). */
public class Certificado {

    private int idCertificado;
    private String codigo;
    private Inscripcion inscripcion;
    private LocalDate fechaEmision;
    private String rutaArchivo;

    public Certificado() { }

    public Certificado(String codigo, Inscripcion inscripcion, LocalDate fechaEmision) {
        setCodigo(codigo);
        setInscripcion(inscripcion);
        setFechaEmision(fechaEmision);
    }

    public int getIdCertificado() { return idCertificado; }

    public void setIdCertificado(int idCertificado) { this.idCertificado = idCertificado; }

    public String getCodigo() { return codigo; }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo del certificado es obligatorio.");
        }
        this.codigo = codigo.trim().toUpperCase();
    }

    public Inscripcion getInscripcion() { return inscripcion; }

    public void setInscripcion(Inscripcion inscripcion) {
        if (inscripcion == null) {
            throw new IllegalArgumentException("El certificado necesita una inscripcion.");
        }
        if (!inscripcion.puedeCertificarse()) {
            throw new IllegalStateException(
                    "Solo se certifica a un alumno aprobado. Estado actual: " + inscripcion.getEstado());
        }
        this.inscripcion = inscripcion;
    }

    public LocalDate getFechaEmision() { return fechaEmision; }

    public void setFechaEmision(LocalDate fechaEmision) {
        if (fechaEmision == null) throw new IllegalArgumentException("La fecha de emision es obligatoria.");
        this.fechaEmision = fechaEmision;
    }

    public String getRutaArchivo() { return rutaArchivo; }

    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Certificado)) return false;
        return Objects.equals(codigo, ((Certificado) o).codigo);
    }

    @Override
    public int hashCode() { return Objects.hash(codigo); }

    @Override
    public String toString() { return codigo; }
}
